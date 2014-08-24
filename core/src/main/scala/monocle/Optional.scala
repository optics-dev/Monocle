package monocle

import scalaz.{Applicative, \/}

/**
 * Optional can be seen as a partial Lens - Lens toward an Option - or
 * a 0-1 Traversal. The latter constraint is not enforce at compile time
 * but by OptionalLaws
 */
trait Optional[S, T, A, B] extends Traversal[S, T, A, B] { self =>

  final def getOption(from: S): Option[A] = headOption(from)

  final def modifyOption(from: S, f: A => B): Option[T] = getOption(from).map(a => set(from, f(a)))
  final def modifyOptionF(f: A => B): S => Option[T] = modifyOption(_, f)

  final def setOption(from: S, newValue: B): Option[T] = modifyOption(from, _ => newValue)
  final def setOptionF(newValue: B): S => Option[T] = setOption(_, newValue)

  final def asOptional: Optional[S, T, A, B] = self

  /** non overloaded compose function */
  final def composeOptional[C, D](other: Optional[A, B, C, D]): Optional[S, T, C, D] = new Optional[S, T, C, D] {
    def _traversal[F[_] : Applicative](s: S, f: C => F[D]): F[T] = self.multiLift(s, other.multiLift(_, f))
  }

  @deprecated("Use composeOptional", since = "0.5")
  def compose[C, D](other: Optional[A, B, C, D]): Optional[S, T, C, D] = composeOptional(other)

}

object Optional {

  def apply[S, T, A, B](seta: S => T \/ A, _set: (S, B) => T): Optional[S, T, A, B] = new Optional[S, T, A, B] {
    def _traversal[F[_] : Applicative](from: S, f: A => F[B]): F[T] =
      seta(from)                                   // T    \/ A
        .map(f)                                    // T    \/ F[B]
        .map(Applicative[F].map(_)(_set(from, _))) // T    \/ F[T]
        .leftMap(Applicative[F].point(_))          // F[T] \/ F[T]
        .fold(identity, identity)                  // F[T]
  }

}
