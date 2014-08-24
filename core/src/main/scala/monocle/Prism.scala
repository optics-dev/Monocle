package monocle

import monocle.internal.{ProChoice, Tagged}

import scalaz.Id.Id
import scalaz.{Applicative, \/}

/**
 * A Prism is a special case of Traversal where the focus is limited to
 * 0 or 1 A. In addition, a Prism defines a reverse relation such as
 * you can always get T from B.
 */
trait Prism[S, T, A, B] extends Optional[S, T, A, B] { self =>

  def _prism[P[_, _]: ProChoice, F[_]: Applicative](pafb: P[A, F[B]]): P[S, F[T]]

  def _traversal[F[_]: Applicative](s: S, f: A => F[B]): F[T] =
    _prism[Function1, F](f).apply(s)



  final def reverseGet(b: B): T = _prism[Tagged, Id](Tagged(b)).untagged

  final def re: Getter[B, T] = Getter(reverseGet)

  final def asPrism: Prism[S, T, A, B] = self

  /** non overloaded compose function */
  final def composePrism[C, D](other: Prism[A, B, C, D]): Prism[S, T, C, D] = new Prism[S, T, C, D]{
    def _prism[P[_, _]: ProChoice, F[_]: Applicative](pcfd: P[C, F[D]]): P[S, F[T]] =
      (self._prism[P, F] _ compose other._prism[P, F])(pcfd)
  }

  @deprecated("Use composePrism", since = "0.5")
  def compose[C, D](other: Prism[A, B, C, D]): Prism[S, T, C, D] = composePrism(other)

}

object Prism extends PrismFunctions {

  def apply[S, T, A, B](seta: S => T \/ A, _reverseGet: B => T): Prism[S, T, A, B] = new Prism[S, T, A, B] {
    def _prism[P[_, _], F[_]](pafb: P[A, F[B]])(implicit p: ProChoice[P], f: Applicative[F]): P[S, F[T]] =
      p.mapsnd(p.mapfst[T \/ A, T \/ F[B], S](p.right(pafb))(seta))(_.fold(f.point[T](_), f.map(_)(_reverseGet)))
  }

}

trait PrismFunctions {
  final def isMatching[S, T, A, B](prism: Prism[S, T, A, B])(s: S): Boolean =
    prism.getOption(s).isDefined
}
