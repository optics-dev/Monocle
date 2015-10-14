package monocle.function

import monocle.{Iso, Lens}

import scala.annotation.implicitNotFound

@implicitNotFound("Could not find an instance of Field3[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Field3[S, A] extends Serializable {
  /** Creates a Lens from S to it is third element */
  def third: Lens[S, A]
}

object Field3 extends Field3Functions {
  def fromIso[S, A, B](iso: Iso[S, A])(implicit ev: Field3[A, B]): Field3[S, B] = new Field3[S, B] {
    override def third: Lens[S, B] =
      iso composeLens ev.third
  }
}

trait Field3Functions {
  def third[S, A](implicit ev: Field3[S, A]): Lens[S, A] = ev.third
}