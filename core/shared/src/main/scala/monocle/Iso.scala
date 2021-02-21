package monocle

import cats.{Applicative, Eq, Functor, Monoid}
import cats.arrow.Category
import cats.evidence.{<~<, Is}
import cats.syntax.either._
import monocle.function.{At, Each, FilterIndex, Index}

import scala.annotation.unchecked.uncheckedVariance

/** [[Iso]] is a type alias for [[PIso]] where `S` = `A` and `T` = `B`:
  * {{{
  * type Iso[S, A] = PIso[S, S, A, A]
  * }}}
  *
  * An [[Iso]] defines an isomorphism between a type S and A:
  * <pre>
  *             get
  *     -------------------->
  *   S                       A
  *     <--------------------
  *          reverseGet
  * </pre>
  *
  * A [[PIso]] allows to lift a function `f: A => B` to `S => T` and a function `g: T => S` to `B => A`
  * <pre>
  *                                                           g
  *     S           T                                   S <-------- T
  *     |           ↑                                   |           ↑
  *     |           |                                   |           |
  * get |           | reverseGet     reverse.reverseGet |           | reverse.get
  *     |           |                                   |           |
  *     ↓     f     |                                   ↓           |
  *     A --------> B                                   A           B
  * </pre>
  *
  * A [[PIso]] is also a valid [[Getter]], [[Fold]], [[PLens]], [[PPrism]], [[POptional]], [[PTraversal]] and [[PSetter]]
  *
  * @see [[monocle.law.IsoLaws]]
  *
  * @tparam S the source of a [[PIso]]
  * @tparam T the modified source of a [[PIso]]
  * @tparam A the target of a [[PIso]]
  * @tparam B the modified target of a [[PIso]]
  */
trait PIso[-S, +T, +A, -B] extends PLens[S, T, A, B] with PPrism[S, T, A, B] { self =>

  /** reverse a [[PIso]]: the source becomes the target and the target becomes the source */
  def reverse: PIso[B, A, T, S]

  /** lift a [[PIso]] to a Functor level */
  def mapping[F[_]: Functor]: PIso[F[S], F[T], F[A], F[B]] @uncheckedVariance =
    PIso[F[S], F[T], F[A], F[B]](fs => Functor[F].map(fs)(self.get))(fb => Functor[F].map(fb)(self.reverseGet))

  override def foldMap[M: Monoid](f: A => M)(s: S): M =
    f(get(s))

  /** find if the target satisfies the predicate */
  override def find(p: A => Boolean): S => Option[A] =
    s => Some(get(s)).filter(p)

  /** check if the target satisfies the predicate */
  override def exist(p: A => Boolean): S => Boolean =
    p compose get

  /** modify polymorphically the target of a [[PIso]] with a Functor function */
  override def modifyF[F[_]: Functor](f: A => F[B] @uncheckedVariance)(s: S): F[T] @uncheckedVariance =
    Functor[F].map(f(get(s)))(reverseGet)

  override def modifyA[F[_]: Applicative](f: A => F[B] @uncheckedVariance)(s: S): F[T] @uncheckedVariance =
    modifyF(f)(s)

  /** modify polymorphically the target of a [[PIso]] with a function */
  override def modify(f: A => B): S => T =
    s => reverseGet(f(get(s)))

  /** replace polymorphically the target of a [[PIso]] with a value */
  override def replace(b: B): S => T =
    _ => reverseGet(b)

  /** pair two disjoint [[PIso]] */
  def split[S1, T1, A1, B1](other: PIso[S1, T1, A1, B1]): PIso[(S, S1), (T, T1), (A, A1), (B, B1)] =
    PIso[(S, S1), (T, T1), (A, A1), (B, B1)] { case (s, s1) =>
      (get(s), other.get(s1))
    } { case (b, b1) =>
      (reverseGet(b), other.reverseGet(b1))
    }

  override def first[C]: PIso[(S, C), (T, C), (A, C), (B, C)] =
    PIso[(S, C), (T, C), (A, C), (B, C)] { case (s, c) =>
      (get(s), c)
    } { case (b, c) =>
      (reverseGet(b), c)
    }

  override def second[C]: PIso[(C, S), (C, T), (C, A), (C, B)] =
    PIso[(C, S), (C, T), (C, A), (C, B)] { case (c, s) =>
      (c, get(s))
    } { case (c, b) =>
      (c, reverseGet(b))
    }

  override def left[C]: PIso[Either[S, C], Either[T, C], Either[A, C], Either[B, C]] =
    PIso[Either[S, C], Either[T, C], Either[A, C], Either[B, C]](_.leftMap(get))(_.leftMap(reverseGet))

  override def right[C]: PIso[Either[C, S], Either[C, T], Either[C, A], Either[C, B]] =
    PIso[Either[C, S], Either[C, T], Either[C, A], Either[C, B]](_.map(get))(_.map(reverseGet))

  override def some[A1, B1](implicit ev1: A <:< Option[A1], ev2: Option[B1] <:< B): PPrism[S, T, A1, B1] =
    adapt[Option[A1], Option[B1]].andThen(std.option.pSome[A1, B1])

  override private[monocle] def adapt[A1, B1](implicit evA: A <:< A1, evB: B1 <:< B): PIso[S, T, A1, B1] =
    asInstanceOf[PIso[S, T, A1, B1]]

  /** compose a [[PIso]] with another [[PIso]] */
  def andThen[C, D](other: PIso[A, B, C, D]): PIso[S, T, C, D] =
    new PIso[S, T, C, D] { composeSelf =>
      def get(s: S): C =
        other.get(self.get(s))

      def reverseGet(d: D): T =
        self.reverseGet(other.reverseGet(d))

      def reverse: PIso[D, C, T, S] =
        new PIso[D, C, T, S] {
          def get(d: D): T =
            self.reverseGet(other.reverseGet(d))

          def reverseGet(s: S): C =
            other.get(self.get(s))

          def reverse: PIso[S, T, C, D] =
            composeSelf
        }
    }

  /** Compose with a function lifted into a Getter */
  override def to[C](f: A => C): Getter[S, C] = andThen(Getter(f))

  /** *************************************************************
    */
  /** Transformation methods to view a [[PIso]] as another Optics */
  /** *************************************************************
    */
  /** view a [[PIso]] as a [[Fold]] */
  override def asFold: Fold[S, A] = this

  /** view a [[PIso]] as a [[Setter]] */
  override def asSetter: PSetter[S, T, A, B] = this

  /** view a [[PIso]] as a [[PTraversal]] */
  override def asTraversal: PTraversal[S, T, A, B] = this

  /** view a [[PIso]] as a [[POptional]] */
  override def asOptional: POptional[S, T, A, B] = this

  /** view a [[PIso]] as a [[PPrism]] */
  def asPrism: PPrism[S, T, A, B] = this

  /** view a [[PIso]] as a [[PLens]] */
  def asLens: PLens[S, T, A, B] = this

  /** **********************************************************************
    */
  /** Apply methods to treat a [[PIso]] as smart constructors for type T */
  /** **********************************************************************
    */
  override def apply[B1 <: B]()(implicit ev: Is[B1, Unit]): T =
    ev.substitute[PIso[S, T, A, *]](self).reverseGet(())

  override def apply(b: B): T = reverseGet(b)

  override def apply[C, D](c: C, d: D)(implicit ev: (C, D) <~< B): T = apply(ev((c, d)))

  override def apply[C, D, E](c: C, d: D, e: E)(implicit ev: (C, D, E) <~< B): T =
    apply(ev((c, d, e)))

  override def apply[C, D, E, F](c: C, d: D, e: E, f: F)(implicit ev: (C, D, E, F) <~< B): T =
    apply(ev((c, d, e, f)))

  override def apply[C, D, E, F, G](c: C, d: D, e: E, f: F, g: G)(implicit ev: (C, D, E, F, G) <~< B): T =
    apply(ev((c, d, e, f, g)))

  override def apply[C, D, E, F, G, H](c: C, d: D, e: E, f: F, g: G, h: H)(implicit ev: (C, D, E, F, G, H) <~< B): T =
    apply(ev((c, d, e, f, g, h)))

  override def unapply(obj: S): Some[A] = Some(get(obj))
}

object PIso extends IsoInstances {

  /** create a [[PIso]] using a pair of functions: one to get the target and one to get the source. */
  def apply[S, T, A, B](_get: S => A)(_reverseGet: B => T): PIso[S, T, A, B] =
    new PIso[S, T, A, B] { self =>
      def get(s: S): A =
        _get(s)

      def reverseGet(b: B): T =
        _reverseGet(b)

      def reverse: PIso[B, A, T, S] =
        new PIso[B, A, T, S] {
          def get(b: B): T =
            _reverseGet(b)

          def reverseGet(s: S): A =
            _get(s)

          def reverse: PIso[S, T, A, B] =
            self
        }
    }

  /** create a [[PIso]] between any type and itself. id is the zero element of optics composition,
    * for all optics o of type O (e.g. Lens, Iso, Prism, ...):
    * o      composeIso Iso.id == o
    * Iso.id composeO   o        == o (replace composeO by composeLens, composeIso, composePrism, ...)
    */
  def id[S, T]: PIso[S, T, S, T] =
    new PIso[S, T, S, T] { self =>
      def get(s: S): S        = s
      def reverseGet(t: T): T = t
      def reverse: PIso[T, S, T, S] =
        new PIso[T, S, T, S] {
          def get(t: T): T              = t
          def reverseGet(s: S): S       = s
          def reverse: PIso[S, T, S, T] = self
        }
    }

  implicit def pIsoSyntax[S, T, A, B](self: PIso[S, T, A, B]): PIsoSyntax[S, T, A, B] =
    new PIsoSyntax(self)

  implicit def isoSyntax[S, A](self: Iso[S, A]): IsoSyntax[S, A] =
    new IsoSyntax(self)
}

object Iso {

  /** alias for [[PIso]] apply when S = T and A = B */
  def apply[S, A](get: S => A)(reverseGet: A => S): Iso[S, A] =
    PIso(get)(reverseGet)

  /** alias for [[PIso]] id when S = T and A = B */
  def id[S]: Iso[S, S] =
    PIso.id[S, S]

  /** create an [[Iso]] from a function that is its own inverse */
  def involuted[A](update: A => A): Iso[A, A] =
    Iso(update)(update)
}

sealed abstract class IsoInstances {
  implicit val isoCategory: Category[Iso] = new Category[Iso] {
    def id[A]: Iso[A, A] =
      Iso.id[A]

    def compose[A, B, C](f: Iso[B, C], g: Iso[A, B]): Iso[A, C] =
      g.andThen(f)
  }
}

final case class PIsoSyntax[S, T, A, B](private val self: PIso[S, T, A, B]) extends AnyVal {
  /** compose a [[PIso]] with a [[Fold]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeFold[C](other: Fold[A, C]): Fold[S, C] =
    self.andThen(other)

  /** compose a [[PIso]] with a [[Getter]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeGetter[C](other: Getter[A, C]): Getter[S, C] =
    self.andThen(other)

  /** compose a [[PIso]] with a [[PSetter]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeSetter[C, D](other: PSetter[A, B, C, D]): PSetter[S, T, C, D] =
    self.andThen(other)

  /** compose a [[PIso]] with a [[PTraversal]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeTraversal[C, D](other: PTraversal[A, B, C, D]): PTraversal[S, T, C, D] =
    self.andThen(other)

  /** compose a [[PIso]] with a [[POptional]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeOptional[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] =
    self.andThen(other)

  /** compose a [[PIso]] with a [[PPrism]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composePrism[C, D](other: PPrism[A, B, C, D]): PPrism[S, T, C, D] =
    self.andThen(other)

  /** compose a [[PIso]] with a [[PLens]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeLens[C, D](other: PLens[A, B, C, D]): PLens[S, T, C, D] =
    self.andThen(other)

  /** compose a [[PIso]] with a [[PIso]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeIso[C, D](other: PIso[A, B, C, D]): PIso[S, T, C, D] =
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
  def ^<-?[C, D](other: PPrism[A, B, C, D]): PPrism[S, T, C, D] =
    self.andThen(other)

  /** alias to composeLens */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|->[C, D](other: PLens[A, B, C, D]): PLens[S, T, C, D] =
    self.andThen(other)

  /** alias to composeIso */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<->[C, D](other: PIso[A, B, C, D]): PIso[S, T, C, D] =
    self.andThen(other)
}

/** Extension methods for monomorphic Iso */
final case class IsoSyntax[S, A](private val self: Iso[S, A]) extends AnyVal {
  def each[C](implicit evEach: Each[A, C]): Traversal[S, C] =
    self.andThen(evEach.each)

  /** Select all the elements which satisfies the predicate.
    * This combinator can break the fusion property see Optional.filter for more details.
    */
  def filter(predicate: A => Boolean): Optional[S, A] =
    self.andThen(Optional.filter(predicate))

  def filterIndex[I, A1](predicate: I => Boolean)(implicit ev: FilterIndex[A, I, A1]): Traversal[S, A1] =
    self.andThen(ev.filterIndex(predicate))

  def withDefault[A1: Eq](defaultValue: A1)(implicit evOpt: A =:= Option[A1]): Iso[S, A1] =
    self.adapt(evOpt, evOpt.flip).andThen(std.option.withDefault(defaultValue))

  def at[I, A1](i: I)(implicit evAt: At[A, i.type, A1]): Lens[S, A1] =
    self.andThen(evAt.at(i))

  def index[I, A1](i: I)(implicit evIndex: Index[A, I, A1]): Optional[S, A1] =
    self.andThen(evIndex.index(i))
}
