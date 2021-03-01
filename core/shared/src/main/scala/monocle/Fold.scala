package monocle

import cats.arrow.Choice
import cats.{Foldable, Monoid}
import monocle.function.{At, Each, FilterIndex, Index}

/** A [[Fold]] can be seen as a [[Getter]] with many targets or
  * a weaker [[PTraversal]] which cannot modify its target.
  *
  * [[Fold]] is on the top of the Optic hierarchy which means that
  * [[Getter]], [[PTraversal]], [[POptional]], [[PLens]], [[PPrism]]
  * and [[PIso]] are valid [[Fold]]
  *
  * @tparam S the source of a [[Fold]]
  * @tparam A the target of a [[Fold]]
  */
trait Fold[S, A] extends Serializable { self =>

  def iterator(from: S): Iterator[A]

  /** map each target to a Monoid and combine the results
    * underlying representation of [[Fold]], all [[Fold]] methods are defined in terms of foldMap
    */
  def foldMap[M: Monoid](f: A => M)(s: S): M =
    iterator(s).foldLeft(Monoid[M].empty)((state, a) => Monoid[M].combine(state, f(a)))

  /** combine all targets using a target's Monoid */
  def fold(s: S)(implicit ev: Monoid[A]): A =
    foldMap(identity)(s)

  /** get all the targets of a [[Fold]] */
  def getAll(s: S): List[A] =
    iterator(s).toList

  /** find the first target matching the predicate */
  def find(p: A => Boolean): S => Option[A] =
    iterator(_).find(p)

  /** get the first target */
  def headOption(s: S): Option[A] = {
    val it = iterator(s)
    if(it.hasNext) Some(it.next())
    else None
  }

  /** get the last target */
  def lastOption(s: S): Option[A] = {
    var last: Option[A] = None
    iterator(s).foreach(value => last = Some(value))
    last
  }

  /** check if at least one target satisfies the predicate */
  def exist(p: A => Boolean): S => Boolean =
    iterator(_).exists(p)

  /** check if all targets satisfy the predicate */
  def all(p: A => Boolean): S => Boolean =
    iterator(_).forall(p)

  /** calculate the number of targets */
  def length(s: S): Int =
    iterator(s).size

  /** check if there is no target */
  def isEmpty(s: S): Boolean =
    iterator(s).isEmpty

  /** check if there is at least one target */
  def nonEmpty(s: S): Boolean =
    iterator(s).nonEmpty

  /** join two [[Fold]] with the same target */
  def choice[S1](other: Fold[S1, A]): Fold[Either[S, S1], A] =
    new Fold[Either[S, S1], A] {
      def iterator(from: Either[S, S1]): Iterator[A] =
        from.fold(self.iterator, other.iterator)
    }

  def left[C]: Fold[Either[S, C], Either[A, C]] =
    new Fold[Either[S, C], Either[A, C]] {
      def iterator(from: Either[S, C]): Iterator[Either[A, C]] =
        from.fold(
          self.iterator(_).map(Left(_)),
          c => Iterator.single(Right(c))
        )
    }

  def right[C]: Fold[Either[C, S], Either[C, A]] =
    new Fold[Either[C, S], Either[C, A]] {
      def iterator(from: Either[C, S]): Iterator[Either[C, A]] =
        from.fold(
          c => Iterator.single(Left(c)),
          self.iterator(_).map(Right(_))
        )
    }

  /** Compose with a function lifted into a Getter */
  def to[C](f: A => C): Fold[S, C] =
    andThen(Getter(f))

  def some[A1](implicit ev1: A =:= Option[A1]): Fold[S, A1] =
    adapt[Option[A1]].andThen(std.option.some[A1])

  private[monocle] def adapt[A1](implicit evA: A =:= A1): Fold[S, A1] =
    evA.substituteCo[Fold[S, *]](this)

  /** compose a [[Fold]] with another [[Fold]] */
  def andThen[B](other: Fold[A, B]): Fold[S, B] =
    new Fold[S, B] {
      def iterator(from: S): Iterator[B] =
        self.iterator(from).flatMap(other.iterator)
    }

}

object Fold extends FoldInstances {
  @deprecated("use Iso.id", since = "3.0.0-M2")
  def id[A]: Fold[A, A] =
    Iso.id[A]

  def codiagonal[A]: Fold[Either[A, A], A] =
    new Fold[Either[A, A], A] {
      def iterator(from: Either[A, A]): Iterator[A] =
        from.fold(Iterator.single, Iterator.single)
    }

  def select[A](p: A => Boolean): Fold[A, A] =
    new Fold[A, A] {
      def iterator(from: A): Iterator[A] =
        if(p(from)) Iterator.single(from) else Iterator.empty
    }

  /** [[Fold]] that points to nothing */
  @deprecated("use Optional.void", since = "3.0.0-M2")
  def void[S, A]: Fold[S, A] =
    Optional.void

  /** create a [[Fold]] from a Foldable */
  def fromFoldable[F[_]: Foldable, A]: Fold[F[A], A] =
    new Fold[F[A], A] {
      def iterator(from: F[A]): Iterator[A] =
        Foldable[F].toIterable(from).iterator

      override def foldMap[M: Monoid](f: A => M)(s: F[A]): M =
        Foldable[F].foldMap(s)(f)
    }

  implicit def foldSyntax[S, A](self: Fold[S, A]): FoldSyntax[S, A] =
    new FoldSyntax(self)
}

sealed abstract class FoldInstances {
  implicit val foldChoice: Choice[Fold] = new Choice[Fold] {
    def choice[A, B, C](f: Fold[A, C], g: Fold[B, C]): Fold[Either[A, B], C] =
      f choice g

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

  /** Select all the elements which satisfies the predicate.
    * This combinator can break the fusion property see Optional.filter for more details.
    */
  def filter(predicate: A => Boolean): Fold[S, A] =
    self.andThen(Optional.filter(predicate))

  def filterIndex[I, A1](predicate: I => Boolean)(implicit ev: FilterIndex[A, I, A1]): Fold[S, A1] =
    self.andThen(ev.filterIndex(predicate))

  def withDefault[A1](defaultValue: A1)(implicit evOpt: A =:= Option[A1]): Fold[S, A1] =
    self.adapt[Option[A1]].andThen(std.option.withDefault(defaultValue))

  def at[I, A1](i: I)(implicit evAt: At[A, I, A1]): Fold[S, A1] =
    self.andThen(evAt.at(i))

  def index[I, A1](i: I)(implicit evIndex: Index[A, I, A1]): Fold[S, A1] =
    self.andThen(evIndex.index(i))

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
