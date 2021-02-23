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
trait PLens[S, T, A, B] extends POptional[S, T, A, B] with Getter[S, A] { self =>

  /** get the target of a [[PLens]] */
  def get(s: S): A

  /** replace polymorphically the target of a [[PLens]] using a function */
  def replace(b: B): S => T

  /** modify polymorphically the target of a [[PLens]] using Functor function */
  def modifyF[F[_]: Functor](f: A => F[B])(s: S): F[T]

  /** modify polymorphically the target of a [[PLens]] using a function */
  def modify(f: A => B): S => T

  def getOrModify(s: S): Either[T, A] = Right(get(s))

  def getOption(s: S): Option[A] = Some(get(s))

  override def modifyA[F[_]: Applicative](f: A => F[B])(s: S): F[T] =
    modifyF(f)(s)

  override def foldMap[M: Monoid](f: A => M)(s: S): M =
    f(get(s))

  /** find if the target satisfies the predicate */
  override def find(p: A => Boolean): S => Option[A] =
    s => Some(get(s)).filter(p)

  /** check if the target satisfies the predicate */
  override def exist(p: A => Boolean): S => Boolean =
    p compose get

  /** join two [[PLens]] with the same target */
  def choice[S1, T1](other: PLens[S1, T1, A, B]): PLens[Either[S, S1], Either[T, T1], A, B] =
    PLens[Either[S, S1], Either[T, T1], A, B](_.fold(self.get, other.get))(b =>
      _.bimap(self.replace(b), other.replace(b))
    )

  /** pair two disjoint [[PLens]] */
  def split[S1, T1, A1, B1](other: PLens[S1, T1, A1, B1]): PLens[(S, S1), (T, T1), (A, A1), (B, B1)] =
    PLens[(S, S1), (T, T1), (A, A1), (B, B1)] { case (s, s1) =>
      (self.get(s), other.get(s1))
    } {
      case (b, b1) => { case (s, s1) =>
        (self.replace(b)(s), other.replace(b1)(s1))
      }
    }

  override def first[C]: PLens[(S, C), (T, C), (A, C), (B, C)] =
    PLens[(S, C), (T, C), (A, C), (B, C)] { case (s, c) =>
      (get(s), c)
    } {
      case (b, c) => { case (s, _) =>
        (replace(b)(s), c)
      }
    }

  override def second[C]: PLens[(C, S), (C, T), (C, A), (C, B)] =
    PLens[(C, S), (C, T), (C, A), (C, B)] { case (c, s) =>
      (c, get(s))
    } {
      case (c, b) => { case (_, s) =>
        (c, replace(b)(s))
      }
    }

  override def some[A1, B1](implicit ev1: A =:= Option[A1], ev2: B =:= Option[B1]): POptional[S, T, A1, B1] =
    adapt[Option[A1], Option[B1]].andThen(std.option.pSome[A1, B1])

  override private[monocle] def adapt[A1, B1](implicit evA: A =:= A1, evB: B =:= B1): PLens[S, T, A1, B1] =
    evB.substituteCo[PLens[S, T, A1, *]](evA.substituteCo[PLens[S, T, *, B]](this))

  /** compose a [[PLens]] with a [[PLens]] */
  def andThen[C, D](other: PLens[A, B, C, D]): PLens[S, T, C, D] =
    new PLens[S, T, C, D] {
      def get(s: S): C =
        other.get(self.get(s))

      override def replace(d: D): S => T =
        self.modify(other.replace(d))

      def modifyF[F[_]: Functor](f: C => F[D])(s: S): F[T] =
        self.modifyF(other.modifyF(f))(s)

      override def modify(f: C => D): S => T =
        self.modify(other.modify(f))
    }

  /** Compose with a function lifted into a Getter */
  override def to[C](f: A => C): Getter[S, C] = andThen(Getter(f))

  /** *********************************************************************************************
    */
  /** Transformation methods to view a [[PLens]] as another Optics */
  /** *********************************************************************************************
    */
  /** view a [[PLens]] as a [[Fold]] */
  override def asFold: Fold[S, A] = this

  /** view a [[PLens]] as a [[Getter]] */
  def asGetter: Getter[S, A] = this

  /** view a [[PLens]] as an [[POptional]] */
  def asOptional: POptional[S, T, A, B] = this
}

object PLens extends LensInstances {
  @deprecated("use PIso.id", since = "3.0.0-M2")
  def id[S, T]: PLens[S, T, S, T] =
    PIso.id[S, T]

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

      override def replace(b: B): S => T =
        _set(b)

      def modifyF[F[_]: Functor](f: A => F[B])(s: S): F[T] =
        Functor[F].map(f(_get(s)))(_set(_)(s))

      override def modify(f: A => B): S => T =
        s => _set(f(_get(s)))(s)
    }

  implicit def pLensSyntax[S, T, A, B](self: PLens[S, T, A, B]): PLensSyntax[S, T, A, B] =
    new PLensSyntax(self)

  implicit def lensSyntax[S, A](self: Lens[S, A]): LensSyntax[S, A] =
    new LensSyntax(self)
}

object Lens {
  @deprecated("use Iso.id", since = "3.0.0-M2")
  def id[A]: Lens[A, A] =
    Iso.id[A]

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
      Iso.id

    def compose[A, B, C](f: Lens[B, C], g: Lens[A, B]): Lens[A, C] =
      g.andThen(f)
  }
}

final case class PLensSyntax[S, T, A, B](private val self: PLens[S, T, A, B]) extends AnyVal {

  /** compose a [[PLens]] with a [[Fold]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeFold[C](other: Fold[A, C]): Fold[S, C] =
    self.andThen(other)

  /** compose a [[PLens]] with a [[Getter]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeGetter[C](other: Getter[A, C]): Getter[S, C] =
    self.andThen(other)

  /** compose a [[PLens]] with a [[PSetter]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeSetter[C, D](other: PSetter[A, B, C, D]): PSetter[S, T, C, D] =
    self.andThen(other)

  /** compose a [[PLens]] with a [[PTraversal]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeTraversal[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] =
    self.andThen(other)

  /** compose a [[PLens]] with an [[POptional]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeOptional[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] =
    self.andThen(other)

  /** compose a [[PLens]] with a [[PPrism]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composePrism[C, D](other: PPrism[A, B, C, D]): POptional[S, T, C, D] =
    self.andThen(other)

  /** compose a [[PLens]] with a [[PLens]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeLens[C, D](other: PLens[A, B, C, D]): PLens[S, T, C, D] =
    self.andThen(other)

  /** compose a [[PLens]] with an [[PIso]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeIso[C, D](other: PIso[A, B, C, D]): PLens[S, T, C, D] =
    self.andThen(other)

  /** alias to composeTraversal */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|->>[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] =
    self.andThen(other)

  /** alias to composeOptional */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|-?[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] =
    self.andThen(other)

  /** alias to composePrism */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<-?[C, D](other: PPrism[A, B, C, D]): POptional[S, T, C, D] =
    self.andThen(other)

  /** alias to composeLens */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|->[C, D](other: PLens[A, B, C, D]): PLens[S, T, C, D] =
    self.andThen(other)

  /** alias to composeIso */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<->[C, D](other: PIso[A, B, C, D]): PLens[S, T, C, D] =
    self.andThen(other)
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
