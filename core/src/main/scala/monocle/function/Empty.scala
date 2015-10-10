package monocle.function

import monocle.{Iso, Prism}

import scala.annotation.implicitNotFound

@implicitNotFound("Could not find an instance of Empty[${S}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Empty[S] extends Serializable {
  def empty: Prism[S, Unit]
}

object Empty extends EmptyFunctions {
  def fromIso[S, A](iso: Iso[S, A])(implicit ev: Empty[A]): Empty[S] = new Empty[S] {
    override def empty: Prism[S, Unit] =
      iso composePrism ev.empty
  }
}

trait EmptyFunctions {
  
  def empty[S](implicit ev: Empty[S]): Prism[S, Unit] =
    ev.empty
  
  def _isEmpty[S](s: S)(implicit ev: Empty[S]): Boolean =
    ev.empty.getOption(s).isDefined

  def _empty[S](implicit ev: Empty[S]): S =
    ev.empty.reverseGet(())
  
}
