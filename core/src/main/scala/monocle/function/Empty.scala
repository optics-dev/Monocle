package monocle.function

import monocle.Prism

import scala.annotation.implicitNotFound

@implicitNotFound("Could not find an instance of Empty[${S}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Empty[S] {
  def empty: Prism[S, Unit]
}

object Empty extends EmptyFunctions

trait EmptyFunctions {
  
  def empty[S](implicit ev: Empty[S]): Prism[S, Unit] = ev.empty
  
  def _isEmpty[S](s: S)(implicit ev: Empty[S]): Boolean = ev.empty.getMaybe(s).isJust

  def _empty[S](implicit ev: Empty[S]): S = ev.empty.reverseGet(())
  
}
