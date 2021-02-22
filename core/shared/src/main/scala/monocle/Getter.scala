package monocle

import cats.{Eq, Monoid, Semigroupal}
import cats.arrow.{Arrow, Choice}
import cats.implicits._
import monocle.function.{At, Each, FilterIndex, Index}

/** A [[Getter]] can be seen as a glorified get method between
  * a type S and a type A.
  *
  * A [[Getter]] is also a valid [[Fold]]
  *
  * @tparam S the source of a [[Getter]]
  * @tparam A the target of a [[Getter]]
  */
trait Getter[S, A] extends Fold[S, A] { self =>

  /** get the target of a [Getter */
  def get(s: S): A

  def foldMap[M: Monoid](f: A => M)(s: S): M =
    f(get(s))

  /** find if the target satisfies the predicate */
  override def find(p: A => Boolean): S => Option[A] =
    s => Some(get(s)).filter(p)

  /** check if the target satisfies the predicate */
  override def exist(p: A => Boolean): S => Boolean =
    p compose get

  /** join two [[Getter]] with the same target */
  def choice[S1, A1 >: A](other: Getter[S1, A1]): Getter[Either[S, S1], A1] =
    Getter[Either[S, S1], A1](_.fold(self.get, other.get))

  /** pair two disjoint [[Getter]] */
  def split[S1, A1](other: Getter[S1, A1]): Getter[(S, S1), (A, A1)] =
    Getter[(S, S1), (A, A1)] { case (s, s1) => (self.get(s), other.get(s1)) }

  def zip[S1 <: S, A1](other: Getter[S1, A1]): Getter[S1, (A, A1)] =
    Getter[S1, (A, A1)](s => (self.get(s), other.get(s)))

  def first[B]: Getter[(S, B), (A, B)] =
    Getter[(S, B), (A, B)] { case (s, b) => (self.get(s), b) }

  def second[B]: Getter[(B, S), (B, A)] =
    Getter[(B, S), (B, A)] { case (b, s) => (b, self.get(s)) }

  override def left[C]: Getter[Either[S, C], Either[A, C]] =
    Getter[Either[S, C], Either[A, C]](_.leftMap(get))

  override def right[C]: Getter[Either[C, S], Either[C, A]] =
    Getter[Either[C, S], Either[C, A]](_.map(get))

  /** Compose with a function lifted into a Getter */
  override def to[C](f: A => C): Getter[S, C] =
    andThen(Getter(f))

  override def some[A1](implicit ev1: A =:= Option[A1]): Fold[S, A1] =
    adapt[Option[A1]].andThen(std.option.some[A1])

  override private[monocle] def adapt[A1](implicit evA: A =:= A1): Getter[S, A1] =
    asInstanceOf[Getter[S, A1]]

  /** compose a [[Getter]] with a [[Getter]] */
  def andThen[B](other: Getter[A, B]): Getter[S, B] =
    (s: S) => other.get(self.get(s))

  /** ***************************************************************
    */
  /** Transformation methods to view a [[Getter]] as another Optics */
  /** ***************************************************************
    */
  /** view a [[Getter]] with a [[Fold]] */
  def asFold: Fold[S, A] =
    new Fold[S, A] {
      def foldMap[M: Monoid](f: A => M)(s: S): M =
        f(get(s))
    }
}

object Getter extends GetterInstances {
  @deprecated("use PIso.id", since = "3.0.0-M2")
  def id[A]: Getter[A, A] =
    Iso.id[A]

  def codiagonal[A]: Getter[Either[A, A], A] =
    Getter[Either[A, A], A](_.fold(identity, identity))

  def apply[S, A](_get: S => A): Getter[S, A] =
    (s: S) => _get(s)

  implicit def getterSyntax[S, A](self: Getter[S, A]): GetterSyntax[S, A] =
    new GetterSyntax(self)
}

sealed abstract class GetterInstances extends GetterInstances0 {
  implicit val getterArrow: Arrow[Getter] = new Arrow[Getter] {
    def lift[A, B](f: (A) => B): Getter[A, B] =
      Getter(f)

    def first[A, B, C](f: Getter[A, B]): Getter[(A, C), (B, C)] =
      f.first

    override def second[A, B, C](f: Getter[A, B]): Getter[(C, A), (C, B)] =
      f.second

    override def id[A]: Getter[A, A] =
      Iso.id

    def compose[A, B, C](f: Getter[B, C], g: Getter[A, B]): Getter[A, C] =
      g.andThen(f)
  }

  implicit def getterSemigroupal[S]: Semigroupal[Getter[S, *]] =
    new Semigroupal[Getter[S, *]] {
      override def product[A, B](a: Getter[S, A], b: Getter[S, B]) = a zip b
    }
}

sealed abstract class GetterInstances0 {
  implicit val getterChoice: Choice[Getter] = new Choice[Getter] {
    def choice[A, B, C](f: Getter[A, C], g: Getter[B, C]): Getter[Either[A, B], C] =
      f choice g

    def id[A]: Getter[A, A] =
      Iso.id

    def compose[A, B, C](f: Getter[B, C], g: Getter[A, B]): Getter[A, C] =
      g.andThen(f)
  }
}

/** Extension methods for Fold */
final case class GetterSyntax[S, A](private val self: Getter[S, A]) extends AnyVal {
  def each[C](implicit evEach: Each[A, C]): Fold[S, C] =
    self.andThen(evEach.each)

  /** Select all the elements which satisfies the predicate.
    * This combinator can break the fusion property see Optional.filter for more details.
    */
  def filter(predicate: A => Boolean): Fold[S, A] =
    self.andThen(Optional.filter(predicate))

  def filterIndex[I, A1](predicate: I => Boolean)(implicit ev: FilterIndex[A, I, A1]): Fold[S, A1] =
    self.andThen(ev.filterIndex(predicate))

  def withDefault[A1: Eq](defaultValue: A1)(implicit evOpt: A =:= Option[A1]): Getter[S, A1] =
    self.adapt[Option[A1]].andThen(std.option.withDefault(defaultValue))

  def at[I, A1](i: I)(implicit evAt: At[A, i.type, A1]): Getter[S, A1] =
    self.andThen(evAt.at(i))

  def index[I, A1](i: I)(implicit evIndex: Index[A, I, A1]): Fold[S, A1] =
    self.andThen(evIndex.index(i))

  /** compose a [[Fold]] with a [[Fold]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeFold[C](other: Fold[A, C]): Fold[S, C] =
    self.andThen(other)

  /** compose a [[Fold]] with a [[Getter]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeGetter[C](other: Getter[A, C]): Getter[S, C] =
    self.andThen(other)

  /** compose a [[Fold]] with a [[PTraversal]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeTraversal[B, C, D](other: PTraversal[A, B, C, D]): Fold[S, C] =
    self.andThen(other)

  /** compose a [[Fold]] with a [[POptional]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeOptional[B, C, D](other: POptional[A, B, C, D]): Fold[S, C] =
    self.andThen(other)

  /** compose a [[Fold]] with a [[PPrism]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composePrism[B, C, D](other: PPrism[A, B, C, D]): Fold[S, C] =
    self.andThen(other)

  /** compose a [[Fold]] with a [[PLens]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeLens[B, C, D](other: PLens[A, B, C, D]): Getter[S, C] =
    self.andThen(other)

  /** compose a [[Fold]] with a [[PIso]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeIso[B, C, D](other: PIso[A, B, C, D]): Getter[S, C] =
    self.andThen(other)

  /** alias to composeTraversal */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|->>[B, C, D](other: PTraversal[A, B, C, D]): Fold[S, C] =
    self.andThen(other)

  /** alias to composeOptional */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|-?[B, C, D](other: POptional[A, B, C, D]): Fold[S, C] =
    self.andThen(other)

  /** alias to composePrism */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<-?[B, C, D](other: PPrism[A, B, C, D]): Fold[S, C] =
    self.andThen(other)

  /** alias to composeLens */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^|->[B, C, D](other: PLens[A, B, C, D]): Getter[S, C] =
    self.andThen(other)

  /** alias to composeIso */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<->[B, C, D](other: PIso[A, B, C, D]): Getter[S, C] =
    self.andThen(other)
}
