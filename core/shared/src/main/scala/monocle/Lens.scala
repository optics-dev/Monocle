package monocle

import cats.{Applicative, Eq, Functor, Monoid}
import cats.arrow.Choice
import cats.syntax.either._
import monocle.function.{At, Each, FilterIndex, Index}

/** A [[PLens]] can be seen as a pair of functions:
  *  - `get: S      => A` i.e. from an `S`, we can extract an `A`
  *  - `set: (B, S) => T` i.e. if we replace an `A` by a `B` in an `S`, we obtain a `T`
  *
  * A [[PLens]] could also be defined as a weaker [[PIso]] where replace requires
  * an additional parameter than reverseGet.
  *
  * [[PLens]] stands for Polymorphic Lens as it replace and modify methods change
  * a type `A` to `B` and `S` to `T`.
  * [[Lens]] is a type alias for [[PLens]] restricted to monomorphic updates:
  * {{{
  * type Lens[S, A] = PLens[S, S, A, A]
  * }}}
  *
  * A [[PLens]] is also a valid [[Getter]], [[Fold]], [[POptional]],
  * [[PTraversal]] and [[PSetter]]
  *
  * Typically a [[PLens]] or [[Lens]] can be defined between a Product
  * (e.g. case class, tuple, HList) and one of its component.
  *
  * @see [[monocle.law.LensLaws]]
  *
  * @tparam S the source of a [[PLens]]
  * @tparam T the modified source of a [[PLens]]
  * @tparam A the target of a [[PLens]]
  * @tparam B the modified target of a [[PLens]]
  */
abstract class PLens[S, T, A, B] extends Serializable { self =>

  /** get the target of a [[PLens]] */
  def get(s: S): A

  /** replace polymorphically the target of a [[PLens]] using a function */
  def replace(b: B): S => T

  /** alias to replace */
  @deprecated("use replace instead", since = "3.0.0-M1")
  def set(b: B): S => T = replace(b)

  /** modify polymorphically the target of a [[PLens]] using Functor function */
  def modifyF[F[_]: Functor](f: A => F[B])(s: S): F[T]

  /** modify polymorphically the target of a [[PLens]] using a function */
  def modify(f: A => B): S => T

  /** find if the target satisfies the predicate */
  final def find(p: A => Boolean): S => Option[A] =
    s => Some(get(s)).filter(p)

  /** check if the target satisfies the predicate */
  final def exist(p: A => Boolean): S => Boolean =
    p compose get

  /** join two [[PLens]] with the same target */
  final def choice[S1, T1](other: PLens[S1, T1, A, B]): PLens[Either[S, S1], Either[T, T1], A, B] =
    PLens[Either[S, S1], Either[T, T1], A, B](_.fold(self.get, other.get))(b =>
      _.bimap(self.replace(b), other.replace(b))
    )

  /** pair two disjoint [[PLens]] */
  final def split[S1, T1, A1, B1](other: PLens[S1, T1, A1, B1]): PLens[(S, S1), (T, T1), (A, A1), (B, B1)] =
    PLens[(S, S1), (T, T1), (A, A1), (B, B1)] { case (s, s1) =>
      (self.get(s), other.get(s1))
    } {
      case (b, b1) => { case (s, s1) =>
        (self.replace(b)(s), other.replace(b1)(s1))
      }
    }

  final def first[C]: PLens[(S, C), (T, C), (A, C), (B, C)] =
    PLens[(S, C), (T, C), (A, C), (B, C)] { case (s, c) =>
      (get(s), c)
    } {
      case (b, c) => { case (s, _) =>
        (replace(b)(s), c)
      }
    }

  final def second[C]: PLens[(C, S), (C, T), (C, A), (C, B)] =
    PLens[(C, S), (C, T), (C, A), (C, B)] { case (c, s) =>
      (c, get(s))
    } {
      case (c, b) => { case (_, s) =>
        (c, replace(b)(s))
      }
    }

  def some[A1, B1](implicit ev1: A =:= Option[A1], ev2: B =:= Option[B1]): POptional[S, T, A1, B1] =
    adapt[Option[A1], Option[B1]].andThen(std.option.pSome[A1, B1])

  private[monocle] def adapt[A1, B1](implicit evA: A =:= A1, evB: B =:= B1): PLens[S, T, A1, B1] =
    evB.substituteCo[PLens[S, T, A1, *]](evA.substituteCo[PLens[S, T, *, B]](this))

  /** compose a [[PLens]] with a [[Fold]] */
  final def andThen[C](other: Fold[A, C]): Fold[S, C] =
    asFold.andThen(other)

  /** compose a [[PLens]] with a [[Getter]] */
  final def andThen[C](other: Getter[A, C]): Getter[S, C] =
    asGetter.andThen(other)

  /** compose a [[PLens]] with a [[PSetter]] */
  final def andThen[C, D](other: PSetter[A, B, C, D]): PSetter[S, T, C, D] =
    asSetter.andThen(other)

  /** compose a [[PLens]] with a [[PTraversal]] */
  final def andThen[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] =
    asTraversal.andThen(other)

  /** compose a [[PLens]] with an [[POptional]] */
  final def andThen[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] =
    asOptional.andThen(other)

  /** compose a [[PLens]] with a [[PPrism]] */
  final def andThen[C, D](other: PPrism[A, B, C, D]): POptional[S, T, C, D] =
    asOptional.andThen(other)

  /** compose a [[PLens]] with a [[PLens]] */
  final def andThen[C, D](other: PLens[A, B, C, D]): PLens[S, T, C, D] =
    new PLens[S, T, C, D] {
      def get(s: S): C =
        other.get(self.get(s))

      def replace(d: D): S => T =
        self.modify(other.replace(d))

      def modifyF[F[_]: Functor](f: C => F[D])(s: S): F[T] =
        self.modifyF(other.modifyF(f))(s)

      def modify(f: C => D): S => T =
        self.modify(other.modify(f))
    }

  /** compose a [[PLens]] with an [[PIso]] */
  final def andThen[C, D](other: PIso[A, B, C, D]): PLens[S, T, C, D] =
    andThen(other.asLens)

  /** compose a [[PLens]] with a [[Fold]] */
  final def composeFold[C](other: Fold[A, C]): Fold[S, C] =
    andThen(other)

  /** Compose with a function lifted into a Getter */
  def to[C](f: A => C): Getter[S, C] = andThen(Getter(f))

  /** compose a [[PLens]] with a [[Getter]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def composeGetter[C](other: Getter[A, C]): Getter[S, C] =
    andThen(other)

  /** compose a [[PLens]] with a [[PSetter]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def composeSetter[C, D](other: PSetter[A, B, C, D]): PSetter[S, T, C, D] =
    andThen(other)

  /** compose a [[PLens]] with a [[PTraversal]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def composeTraversal[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] =
    andThen(other)

  /** compose a [[PLens]] with an [[POptional]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def composeOptional[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] =
    andThen(other)

  /** compose a [[PLens]] with a [[PPrism]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def composePrism[C, D](other: PPrism[A, B, C, D]): POptional[S, T, C, D] =
    andThen(other)

  /** compose a [[PLens]] with a [[PLens]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def composeLens[C, D](other: PLens[A, B, C, D]): PLens[S, T, C, D] =
    andThen(other)

  /** compose a [[PLens]] with an [[PIso]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def composeIso[C, D](other: PIso[A, B, C, D]): PLens[S, T, C, D] =
    andThen(other)

  /** *****************************************
    */
  /** Experimental aliases of compose methods */
  /** *****************************************
    */
  /** alias to composeTraversal */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def ^|->>[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] =
    andThen(other)

  /** alias to composeOptional */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def ^|-?[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] =
    andThen(other)

  /** alias to composePrism */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def ^<-?[C, D](other: PPrism[A, B, C, D]): POptional[S, T, C, D] =
    andThen(other)

  /** alias to composeLens */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def ^|->[C, D](other: PLens[A, B, C, D]): PLens[S, T, C, D] =
    andThen(other)

  /** alias to composeIso */
  @deprecated("use andThen", since = "3.0.0-M1")
  final def ^<->[C, D](other: PIso[A, B, C, D]): PLens[S, T, C, D] =
    andThen(other)

  /** *********************************************************************************************
    */
  /** Transformation methods to view a [[PLens]] as another Optics */
  /** *********************************************************************************************
    */
  /** view a [[PLens]] as a [[Fold]] */
  final def asFold: Fold[S, A] =
    new Fold[S, A] {
      def foldMap[M: Monoid](f: A => M)(s: S): M =
        f(get(s))
    }

  /** view a [[PLens]] as a [[Getter]] */
  final def asGetter: Getter[S, A] =
    (s: S) => self.get(s)

  /** view a [[PLens]] as a [[PSetter]] */
  final def asSetter: PSetter[S, T, A, B] =
    new PSetter[S, T, A, B] {
      def modify(f: A => B): S => T =
        self.modify(f)

      def replace(b: B): S => T =
        self.replace(b)
    }

  /** view a [[PLens]] as a [[PTraversal]] */
  final def asTraversal: PTraversal[S, T, A, B] =
    new PTraversal[S, T, A, B] {
      def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
        self.modifyF(f)(s)
    }

  /** view a [[PLens]] as an [[POptional]] */
  final def asOptional: POptional[S, T, A, B] =
    new POptional[S, T, A, B] {
      def getOrModify(s: S): Either[T, A] =
        Either.right(get(s))

      def replace(b: B): S => T =
        self.replace(b)

      def getOption(s: S): Option[A] =
        Some(self.get(s))

      def modify(f: A => B): S => T =
        self.modify(f)

      def modifyF[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
        self.modifyF(f)(s)
    }
}

object PLens extends LensInstances {
  def id[S, T]: PLens[S, T, S, T] =
    PIso.id[S, T].asLens

  def codiagonal[S, T]: PLens[Either[S, S], Either[T, T], S, T] =
    PLens[Either[S, S], Either[T, T], S, T](
      _.fold(identity, identity)
    )(t => _.bimap(_ => t, _ => t))

  /** create a [[PLens]] using a pair of functions: one to get the target, one to replace the target.
    * @see macro module for methods generating [[PLens]] with less boiler plate
    */
  def apply[S, T, A, B](_get: S => A)(_set: B => S => T): PLens[S, T, A, B] =
    new PLens[S, T, A, B] {
      def get(s: S): A =
        _get(s)

      def replace(b: B): S => T =
        _set(b)

      def modifyF[F[_]: Functor](f: A => F[B])(s: S): F[T] =
        Functor[F].map(f(_get(s)))(_set(_)(s))

      def modify(f: A => B): S => T =
        s => _set(f(_get(s)))(s)
    }

  implicit def lensSyntax[S, A](self: Lens[S, A]): LensSyntax[S, A] =
    new LensSyntax(self)
}

object Lens {
  def id[A]: Lens[A, A] =
    Iso.id[A].asLens

  def codiagonal[S]: Lens[Either[S, S], S] =
    PLens.codiagonal

  /** alias for [[PLens]] apply with a monomorphic replace function */
  def apply[S, A](get: S => A)(replace: A => S => S): Lens[S, A] =
    PLens(get)(replace)
}

sealed abstract class LensInstances {
  implicit val lensChoice: Choice[Lens] = new Choice[Lens] {
    def choice[A, B, C](f: Lens[A, C], g: Lens[B, C]): Lens[Either[A, B], C] =
      f choice g

    def id[A]: Lens[A, A] =
      Lens.id

    def compose[A, B, C](f: Lens[B, C], g: Lens[A, B]): Lens[A, C] =
      g.andThen(f)
  }
}

/** Extension methods for monomorphic Lens
  */
final case class LensSyntax[S, A](private val self: Lens[S, A]) extends AnyVal {
  def each[C](implicit evEach: Each[A, C]): Traversal[S, C] =
    self.andThen(evEach.each)

  /** Select all the elements which satisfies the predicate.
    * This combinator can break the fusion property see Optional.filter for more details.
    */
  def filter(predicate: A => Boolean): Optional[S, A] =
    self.andThen(Optional.filter(predicate))

  def filterIndex[I, A1](predicate: I => Boolean)(implicit ev: FilterIndex[A, I, A1]): Traversal[S, A1] =
    self.andThen(ev.filterIndex(predicate))

  def withDefault[A1: Eq](defaultValue: A1)(implicit evOpt: A =:= Option[A1]): Lens[S, A1] =
    self.adapt[Option[A1], Option[A1]].andThen(std.option.withDefault(defaultValue))

  def at[I, A1](i: I)(implicit evAt: At[A, i.type, A1]): Lens[S, A1] =
    self.andThen(evAt.at(i))

  def index[I, A1](i: I)(implicit evIndex: Index[A, I, A1]): Optional[S, A1] =
    self.andThen(evIndex.index(i))
}
