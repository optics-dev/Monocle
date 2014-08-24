package monocle.function

import monocle.SimplePrism

trait Empty[S] {
  def empty: SimplePrism[S, Unit]
}

object Empty extends EmptyFunctions

trait EmptyFunctions {
  
  def empty[S](implicit ev: Empty[S]): SimplePrism[S, Unit] = ev.empty
  
  def _isEmpty[S](s: S)(implicit ev: Empty[S]): Boolean = ev.empty.getOption(s).isDefined

  def _empty[S](implicit ev: Empty[S]): S = ev.empty.reverseGet(())
  
}
