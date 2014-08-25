package monocle

import monocle.internal.{Strong, ProChoice}

import scalaz.{Applicative, Functor, Profunctor}
import scalaz.std.function._
import scalaz.Id.Id

/**
 * An Iso is a Lens that can be reversed and so it defines an isomorphism.
 */
trait Iso[S, T, A, B] extends Lens[S, T, A, B] with Prism[S, T, A, B] { self =>

  def _iso[P[_, _]: Profunctor, F[_]: Functor](pafb: P[A, F[B]]): P[S, F[T]]

  def _lens[P[_, _] : Strong](pab: P[A, B]): P[S, T] = _iso[P, Id](pab)

  final def _prism[P[_, _]: ProChoice, F[_]: Applicative](pafb: P[A, F[B]]): P[S, F[T]] =
    _iso(pafb)

  // need to override because of conflicting inheritance
  final override def _traversal[F[_] : Applicative](s: S, f: A => F[B]): F[T] =
    _iso[Function1, F](f).apply(s)

  final def reverse: Iso[B, A, T, S] = Iso[B, A, T, S](reverseGet, get)

  /** non overloaded compose function */
  final def composeIso[C, D](other: Iso[A, B, C, D]): Iso[S, T, C, D] = new Iso[S, T, C, D]{
    def _iso[P[_, _] : Profunctor, F[_] : Functor](pcfd: P[C, F[D]]): P[S, F[T]] =
      (self._iso[P, F] _ compose other._iso[P, F])(pcfd)
  }

  @deprecated("Use composeIso", since = "0.5")
  def compose[C, D](other: Iso[A, B, C, D]): Iso[S, T, C, D] = composeIso(other)

}

object Iso {

  def apply[S, T, A, B](_get: S => A, _reverseGet: B => T): Iso[S, T, A, B] = new Iso[S, T, A, B] {
    def _iso[P[_, _], F[_]](pafb: P[A, F[B]])(implicit p: Profunctor[P], f: Functor[F]): P[S, F[T]] =
      p.mapsnd(p.mapfst(pafb)(_get))(f.map(_)(_reverseGet))
  }

}
