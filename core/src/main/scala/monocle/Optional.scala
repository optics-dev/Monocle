package monocle

import _root_.scalaz.{Functor, Applicative}

/**
 * Optional can be seen as a partial Lens - Lens toward an Option - or
 * a 0-1 Traversal. The latter constraint is not enforce at compile time
 * but by OptionalLaws
 */
trait Optional[S, T, A, B] extends Traversal[S, T, A, B] { self =>

  def getOption(from: S): Option[A] = headOption(from)

  def modifyOption(from: S, f: A => B): Option[T] = getOption(from).map(a => set(from, f(a)))

  final def modifyOptionF(f: A => B): S => Option[T] = modifyOption(_, f)

  def setOption(from: S, newValue: B): Option[T] = modifyOption(from, _ => newValue)

  final def setOptionF(newValue: B): S => Option[T] = setOption(_, newValue)

  def asOptional: Optional[S, T, A, B] = self

  /** non overloaded compose function */
  def composeOptional[C, D](other: Optional[A, B, C, D]): Optional[S, T, C, D] = new Optional[S, T, C, D] {
    def multiLift[F[_] : Applicative](from: S, f: C => F[D]): F[T] = self.multiLift(from, other.multiLift(_, f))
  }

  @deprecated("Use composeOptional", since = "0.5")
  def compose[C, D](other: Optional[A, B, C, D]): Optional[S, T, C, D] = composeOptional(other)

}

object Optional {

  def apply[S, T, A, B](_getOption: S => Option[A], _set: (S, Option[B]) => T): Optional[S, T, A, B] = new Optional[S, T, A, B] {
    import _root_.scalaz.syntax.traverse._
    import _root_.scalaz.std.option._
    def multiLift[F[_] : Applicative](from: S, f: A => F[B]): F[T] =
     Functor[F].map(_getOption(from).map(f).sequence)(_set(from, _))
  }

}
