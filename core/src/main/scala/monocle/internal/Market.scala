package monocle.internal

import scalaz._


final case class Market[A, B, S, T](getOr: S => T \/ A, reverseGet: B => T)

object Market extends MarketInstances

sealed abstract class MarketInstances1 {
  implicit def marketProFunctor[A, B]: Profunctor[Market[A, B, ?, ?]] = new MarketProFunctor[A, B]{}
}

sealed abstract class MarketInstances extends MarketInstances1 {
  implicit def marketProChoice[A, B]: ProChoice[Market[A, B, ?, ?]] = new MarketProChoice[A, B] {}
}


private sealed trait MarketProFunctor[A, B] extends Profunctor[Market[A, B, ?, ?]]{
  def mapfst[S, T, C](fab: Market[A, B, S, T])(f: C => S): Market[A, B, C, T] =
    fab.copy(getOr = fab.getOr compose f)
  def mapsnd[S, T, C](fab: Market[A, B, S, T])(f: T => C): Market[A, B, S, C] =
    Market(s => fab.getOr(s).leftMap(f), f compose fab.reverseGet)
}

private sealed trait MarketProChoice[A, B] extends ProChoice[Market[A, B, ?, ?]] with MarketProFunctor[A, B]{
  final override def left[S, T, C](pab: Market[A, B, S, T]): Market[A, B, S \/ C, T \/ C] =
    Market(_.fold(s => pab.getOr(s).leftMap(\/.left), c => -\/(\/-(c))), \/.left compose pab.reverseGet)
  final override def right[S, T, C](pab: Market[A, B, S, T]): Market[A, B, C \/ S, C \/ T] =
    Market(_.fold(c => -\/(-\/(c)), s => pab.getOr(s).leftMap(\/.right)), \/.right compose pab.reverseGet)
}
