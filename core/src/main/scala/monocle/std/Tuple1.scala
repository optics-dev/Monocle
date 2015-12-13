package monocle.std

import monocle.function._
import monocle.{Iso, Lens}

object tuple1 extends Tuple1Optics

trait Tuple1Optics {

  def tuple1Iso[A]: Iso[Tuple1[A], A] = Iso[Tuple1[A], A](_._1)(Tuple1.apply)

  implicit def tuple1Field1[A]: Field1[Tuple1[A], A] = new Field1[Tuple1[A], A] {
    def first = Lens((_: Tuple1[A])._1)(a => _ => Tuple1(a))
  }

  implicit def tuple1Reverse[A]: Reverse[Tuple1[A], Tuple1[A]] = new Reverse[Tuple1[A], Tuple1[A]] {
    def reverse = Iso.id
  }

}
