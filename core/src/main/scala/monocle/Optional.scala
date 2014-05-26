package monocle

import scalaz.{Functor, Applicative}

/**
 * Optional can be seen as a partial Lens - Lens toward an Option - or
 * a 0-1 Traversal. The latter constraint is not enforce at compile time
 * but by OptionalLaws
 */
trait Optional[S, T, A, B] extends Traversal[S, T, A, B] { self =>

  def getOption(from: S): Option[A] = headOption(from)

  def asOptional: Optional[S, T, A, B] = self

  /** non overloaded compose function */
  def composeOptional[C, D](other: Optional[A, B, C, D]): Optional[S, T, C, D] = compose(other)

  def compose[C, D](other: Optional[A, B, C, D]): Optional[S, T, C, D] = new Optional[S, T, C, D] {
    def multiLift[F[_] : Applicative](from: S, f: C => F[D]): F[T] = self.multiLift(from, other.multiLift(_, f))
  }

}

object Optional {

  def apply[S, T, A, B](_getOption: S => Option[A], _set: (S, Option[B]) => T): Optional[S, T, A, B] = new Optional[S, T, A, B] {
    import scalaz.syntax.traverse._
    import scalaz.std.option._
    def multiLift[F[_] : Applicative](from: S, f: A => F[B]): F[T] =
     Functor[F].map(_getOption(from).map(f).sequence)(_set(from, _))
  }

}
