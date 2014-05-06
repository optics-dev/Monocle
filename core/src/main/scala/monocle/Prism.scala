package monocle

import scalaz.{ Applicative, \/ }

/**
 * A Prism is a special case of Traversal where the focus is limited to
 * 0 or 1 A. In addition, a Prism defines a reverse relation such as
 * you can always get T from B.
 */
trait Prism[S, T, A, B] extends Traversal[S, T, A, B] { self =>

  def re: Getter[B, T]

  def reverseGet(from: B): T = re.get(from)

  def getOption(from: S): Option[A] = headOption(from)

  def asPrism: Prism[S, T, A, B] = self

  /** non overloaded compose function */
  def composePrism[C, D](other: Prism[A, B, C, D]): Prism[S, T, C, D] = compose(other)

  def compose[C, D](other: Prism[A, B, C, D]): Prism[S, T, C, D] = new Prism[S, T, C, D] {
    def re: Getter[D, T] = other.re compose self.re

    def multiLift[F[_]: Applicative](from: S, f: C => F[D]): F[T] = self.multiLift(from, other.multiLift(_, f))
  }

}

object Prism {

  def apply[S, T, A, B](_reverseGet: B => T, seta: S => T \/ A): Prism[S, T, A, B] = new Prism[S, T, A, B] {
    def re: Getter[B, T] = Getter[B, T](_reverseGet)

    def multiLift[F[_]: Applicative](from: S, f: A => F[B]): F[T] =
      seta(from) // T \/ A
        .map(f) // T \/ F[B]
        .map(Applicative[F].map(_)(_reverseGet)) // T \/ F[T]
        .leftMap(Applicative[F].point(_)) // F[T] \/ F[T]
        .fold(identity, identity) // F[T]
  }

}
