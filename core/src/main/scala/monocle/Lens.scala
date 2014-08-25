package monocle

import monocle.internal.Strong

import scalaz.{Applicative, Const, Functor}

/**
 * A Lens defines a single focus between a type S and A such as if you change A to B
 * you obtain a T.
 */
trait Lens[S, T, A, B] extends Optional[S, T, A, B] with Getter[S, A] { self =>

  def _lens[P[_, _]: Strong](pab: P[A, B]): P[S, T]

  def lift[F[_]: Functor](s: S, f: A => F[B]): F[T] =
    _lens[({ type l[a, b] = a => F[b] })#l](f).apply(s)

  def _traversal[F[_]: Applicative](s: S, f: A => F[B]): F[T] = lift(s, f)


  final def get(s: S): A = lift[({ type l[b] = Const[A, b] })#l](s, a => Const(a)).getConst

  /** non overloaded compose function */
  final def composeLens[C, D](other: Lens[A, B, C, D]): Lens[S, T, C, D] = new Lens[S, T, C, D] {
    def _lens[P[_, _]: Strong](pab: P[C, D]): P[S, T] =
      (self._lens[P] _ compose other._lens[P])(pab)
  }

  @deprecated("Use composeLens", since = "0.5")
  def compose[C, D](other: Lens[A, B, C, D]): Lens[S, T, C, D] = composeLens(other)
}

object Lens {

  def apply[S, T, A, B](_get: S => A, _set: (S, B) => T): Lens[S, T, A, B] = new Lens[S, T, A, B] {
    def _lens[P[_, _]](pab: P[A, B])(implicit p: Strong[P]): P[S, T] = {
      val psasb: P[(S, A), (S, B)] = p.second[A, B, S](pab)
      val psat : P[(S, A), T]      = p.mapsnd(psasb)(_set.tupled)
      val psb  : P[S ,T]           = p.mapfst(psat)(s => (s, _get(s)))
      psb
    }
  }

}
