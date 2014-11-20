package monocle.function

import monocle.Prism

trait Empty[S] {
  def empty: Prism[S, Unit]
}

object Empty extends EmptyFunctions

trait EmptyFunctions {
  
  def empty[S](implicit ev: Empty[S]): Prism[S, Unit] = ev.empty
  
  def _isEmpty[S](s: S)(implicit ev: Empty[S]): Boolean = ev.empty.getMaybe(s).isJust

  def _empty[S](implicit ev: Empty[S]): S = ev.empty.reverseGet(())
  
}
