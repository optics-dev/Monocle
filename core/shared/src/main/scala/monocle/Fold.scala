package monocle

import cats.{Foldable, Monoid}
import cats.arrow.Choice
import cats.syntax.either._
import monocle.function.{At, Each, FilterIndex, Index}
import monocle.internal.Monoids

/** A [[Fold]] can be seen as a [[Getter]] with many targets or a weaker [[PTraversal]] which cannot modify its target.
  *
  * [[Fold]] is on the top of the Optic hierarchy which means that [[Getter]], [[PTraversal]], [[POptional]], [[PLens]],
  * [[PPrism]] and [[PIso]] are valid [[Fold]]
  *
  * @tparam S
  *   the source of a [[Fold]]
  * @tparam A
  *   the target of a [[Fold]]
  */
trait Fold[S, A] extends Serializable { self =>

  /** map each target to a Monoid and combine the results underlying representation of [[Fold]], all [[Fold]] methods
    * are defined in terms of foldMap
    */
  def foldMap[M: Monoid](f: A => M)(s: S): M

  /** combine all targets using a target's Monoid */
  def fold(s: S)(implicit ev: Monoid[A]): A =
    foldMap(identity)(s)

  /** get all the targets of a [[Fold]] */
  def getAll(s: S): List[A] =
    foldMap(List(_))(s)

  /** find the first target matching the predicate */
  def find(p: A => Boolean): S => Option[A] =
    foldMap(a => Some(a).filter(p))(_)(using Monoids.firstOption)

  /** get the first target */
  def headOption(s: S): Option[A] =
    foldMap(Option(_))(s)(using Monoids.firstOption)

  /** get the last target */
  def lastOption(s: S): Option[A] =
    foldMap(Option(_))(s)(using Monoids.lastOption)

  /** check if at least one target satisfies the predicate */
  def exist(p: A => Boolean): S => Boolean =
    foldMap(p(_))(_)(using Monoids.any)

  /** check if all targets satisfy the predicate */
  def all(p: A => Boolean): S => Boolean =
    foldMap(p(_))(_)(using Monoids.all)

  /** calculate the number of targets */
  def length(s: S): Int =
    foldMap(_ => 1)(s)

  /** check if there is no target */
  def isEmpty(s: S): Boolean =
    foldMap(_ => false)(s)(using Monoids.all)

  /** check if there is at least one target */
  def nonEmpty(s: S): Boolean =
    !isEmpty(s)

  @deprecated("no replacement", since = "3.0.0-M4")
  def left[C]: Fold[Either[S, C], Either[A, C]] =
    new Fold[Either[S, C], Either[A, C]] {
      override def foldMap[M: Monoid](f: Either[A, C] => M)(s: Either[S, C]): M =
        s.fold(self.foldMap(a => f(Either.left(a))), c => f(Either.right(c)))
    }

  @deprecated("no replacement", since = "3.0.0-M4")
  def right[C]: Fold[Either[C, S], Either[C, A]] =
    new Fold[Either[C, S], Either[C, A]] {
      override def foldMap[M: Monoid](f: Either[C, A] => M)(s: Either[C, S]): M =
        s.fold(c => f(Either.left(c)), self.foldMap(a => f(Either.right(a))))
    }

  /** Compose with a function lifted into a Getter */
  def to[C](f: A => C): Fold[S, C] =
    andThen(Getter(f))

  def some[A1](implicit ev1: A =:= Option[A1]): Fold[S, A1] =
    adapt[Option[A1]].andThen(std.option.some[A1])

  def index[I, A1](i: I)(implicit evIndex: Index[A, I, A1]): Fold[S, A1] =
    self.andThen(evIndex.index(i))

  private[monocle] def adapt[A1](implicit evA: A =:= A1): Fold[S, A1] =
    evA.substituteCo[Fold[S, *]](this)

  /** compose a [[Fold]] with another [[Fold]] */
  def andThen[B](other: Fold[A, B]): Fold[S, B] =
    new Fold[S, B] {
      def foldMap[M: Monoid](f: B => M)(s: S): M =
        self.foldMap(other.foldMap(f)(_))(s)
    }

}

object Fold extends FoldInstances {
  @deprecated("use Iso.id", since = "3.0.0-M2")
  def id[A]: Fold[A, A] =
    Iso.id[A]

  @deprecated("use Lens.codiagonal", since = "3.0.0-M4")
  def codiagonal[A]: Fold[Either[A, A], A] =
    Lens.codiagonal

  def select[A](p: A => Boolean): Fold[A, A] =
    new Fold[A, A] {
      def foldMap[M: Monoid](f: A => M)(s: A): M =
        if (p(s)) f(s) else Monoid[M].empty
    }

  /** [[Fold]] that points to nothing */
  @deprecated("use Optional.void", since = "3.0.0-M2")
  def void[S, A]: Fold[S, A] =
    Optional.void

  /** create a [[Fold]] from a Foldable */
  def fromFoldable[F[_]: Foldable, A]: Fold[F[A], A] =
    new Fold[F[A], A] {
      def foldMap[M: Monoid](f: A => M)(s: F[A]): M =
        Foldable[F].foldMap(s)(f)
    }

  implicit def foldSyntax[S, A](self: Fold[S, A]): FoldSyntax[S, A] =
    new FoldSyntax(self)
}

sealed abstract class FoldInstances {
  implicit val foldChoice: Choice[Fold] = new Choice[Fold] {
    def choice[A, B, C](fold1: Fold[A, C], fold2: Fold[B, C]): Fold[Either[A, B], C] =
      new Fold[Either[A, B], C] {
        def foldMap[M: Monoid](f: C => M)(s: Either[A, B]): M =
          s.fold(fold1.foldMap(f), fold2.foldMap(f))
      }

    def id[A]: Fold[A, A] =
      Iso.id[A]

    def compose[A, B, C](f: Fold[B, C], g: Fold[A, B]): Fold[A, C] =
      g.andThen(f)
  }
}

/** Extension methods for Fold */
final case class FoldSyntax[S, A](private val self: Fold[S, A]) extends AnyVal {
  def each[C](implicit evEach: Each[A, C]): Fold[S, C] =
    self.andThen(evEach.each)

  /** Select all the elements which satisfies the predicate. This combinator can break the fusion property see
    * Optional.filter for more details.
    */
  def filter(predicate: A => Boolean): Fold[S, A] =
    self.andThen(Optional.filter(predicate))

  def filterIndex[I, A1](predicate: I => Boolean)(implicit ev: FilterIndex[A, I, A1]): Fold[S, A1] =
    self.andThen(ev.filterIndex(predicate))

  def withDefault[A1](defaultValue: A1)(implicit evOpt: A =:= Option[A1]): Fold[S, A1] =
    self.adapt[Option[A1]].andThen(std.option.withDefault(defaultValue))

  def at[I, A1](i: I)(implicit evAt: At[A, I, A1]): Fold[S, A1] =
    self.andThen(evAt.at(i))

  @deprecated("Preserved for bincompat", "3.1.0")
  def index[I, A1](i: I, evIndex: Index[A, I, A1]): Fold[S, A1] =
    self.index(i)(evIndex)

  /** compose a [[Fold]] with a [[Fold]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeFold[C](other: Fold[A, C]): Fold[S, C] =
    self.andThen(other)

  /** compose a [[Fold]] with a [[Getter]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeGetter[C](other: Getter[A, C]): Fold[S, C] =
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
  def composeLens[B, C, D](other: PLens[A, B, C, D]): Fold[S, C] =
    self.andThen(other)

  /** compose a [[Fold]] with a [[PIso]] */
  @deprecated("use andThen", since = "3.0.0-M1")
  def composeIso[B, C, D](other: PIso[A, B, C, D]): Fold[S, C] =
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
  def ^|->[B, C, D](other: PLens[A, B, C, D]): Fold[S, C] =
    self.andThen(other)

  /** alias to composeIso */
  @deprecated("use andThen", since = "3.0.0-M1")
  def ^<->[B, C, D](other: PIso[A, B, C, D]): Fold[S, C] =
    self.andThen(other)
}
