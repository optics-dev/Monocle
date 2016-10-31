package monocle.std

import monocle.Iso

object tuple1 extends Tuple1Optics

trait Tuple1Optics {
  def tuple1Iso[A]: Iso[Tuple1[A], A] = Iso[Tuple1[A], A](_._1)(Tuple1.apply)
}
