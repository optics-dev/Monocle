package monocle.function

import monocle._
import scala.annotation.implicitNotFound

@implicitNotFound("Could not find an instance of Field6[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Field6[S, A] extends Serializable {
  /** Creates a Lens from S to it is sixth element */
  def sixth: Lens[S, A]
}

object Field6 extends Field6Functions {
  def fromIso[S, A, B](iso: Iso[S, A])(implicit ev: Field6[A, B]): Field6[S, B] = new Field6[S, B] {
    override def sixth: Lens[S, B] =
      iso composeLens ev.sixth
  }
}

trait Field6Functions {
  def sixth[S, A](implicit ev: Field6[S, A]): Lens[S, A] = ev.sixth
}