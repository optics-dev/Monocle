package monocle.util

import monocle.SimplePrism
import scala.util.Try

object TryPrism {
  def trySimplePrism[S, A](safe: A => S, unsafe: S => A): SimplePrism[S, A] = SimplePrism(safe, s => Try(unsafe(s)).toOption)
}
