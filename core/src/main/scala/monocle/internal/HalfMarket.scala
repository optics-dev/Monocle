package monocle.internal

import scalaz._


final case class HalfMarket[A, S, T](seta: S => T \/ A)

object HalfMarket extends HalfMarketInstances

sealed abstract class HalfMarketInstances1 {
  implicit def halfMarketProFunctor[A]: Profunctor[HalfMarket[A, ?, ?]] = new HalfMarketProFunctor[A]{}
}

sealed abstract class HalfMarketInstances extends HalfMarketInstances1 {
  implicit def halfMarketProChoice[A]: ProChoice[HalfMarket[A, ?, ?]] = new HalfMarketProChoice[A] {}
  implicit def halfMarketStrong[A]: Strong[HalfMarket[A, ?, ?]]       = new HalfMarketStrong[A] {}
}


private sealed trait HalfMarketProFunctor[A] extends Profunctor[HalfMarket[A, ?, ?]]{
  @inline override def dimap[B, C, D, E](fab: HalfMarket[A, B, C])(f: D => B)(g: C => E): HalfMarket[A, D, E] =
    HalfMarket(s => fab.seta(f(s)).leftMap(g))
  @inline def mapfst[S, T, C](fab: HalfMarket[A, S, T])(f: C => S): HalfMarket[A, C, T] =
    HalfMarket(fab.seta compose f)
  @inline def mapsnd[S, T, C](fab: HalfMarket[A, S, T])(f: T => C): HalfMarket[A, S, C] =
    HalfMarket(s => fab.seta(s).leftMap(f))
}

private sealed trait HalfMarketProChoice[A] extends ProChoice[HalfMarket[A, ?, ?]] with HalfMarketProFunctor[A]{
  @inline final override def left[S, T, C](pab: HalfMarket[A, S, T]): HalfMarket[A, S \/ C, T \/ C] =
    HalfMarket(_.fold(s => pab.seta(s).leftMap(\/.left), c => -\/(\/-(c))))
  @inline final override def right[S, T, C](pab: HalfMarket[A, S, T]): HalfMarket[A, C \/ S, C \/ T] =
    HalfMarket(_.fold(c => -\/(-\/(c)), s => pab.seta(s).leftMap(\/.right)))
}

private sealed trait HalfMarketStrong[A] extends Strong[HalfMarket[A, ?, ?]] with HalfMarketProFunctor[A]{
  @inline final override def first[B, C, D](pab: HalfMarket[A, B, C]): HalfMarket[A, (B, D), (C, D)] =
    HalfMarket{ case (b, d) => pab.seta(b).leftMap(_ -> d) }
  @inline final override def second[B, C, D](pab: HalfMarket[A, B, C]): HalfMarket[A, (D, B), (D, C)] =
    HalfMarket{ case (d, b) => pab.seta(b).leftMap(d -> _) }
}