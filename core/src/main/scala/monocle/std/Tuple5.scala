package monocle.std

import monocle.function._
import monocle.{Iso, Lens, PTraversal}

object tuple5 extends Tuple5Optics

trait Tuple5Optics {

  implicit def tuple5Each[A]: Each[(A, A, A, A, A), A] = new Each[(A, A, A, A, A), A] {
    def each =
      PTraversal.apply5[(A, A, A, A, A), (A, A, A, A, A), A, A](_._1,_._2,_._3,_._4,_._5)((b1, b2, b3, b4, b5, _) => (b1, b2, b3, b4, b5))
  }

  implicit def tuple5Field1[A1, A2, A3, A4, A5]: Field1[(A1, A2, A3, A4, A5), A1] = new Field1[(A1, A2, A3, A4, A5), A1] {
    def first = Lens((_: (A1, A2, A3, A4, A5))._1)(a => t => t.copy(_1 = a))
  }

  implicit def tuple5Field2[A1, A2, A3, A4, A5]: Field2[(A1, A2, A3, A4, A5), A2] = new Field2[(A1, A2, A3, A4, A5), A2] {
    def second = Lens((_: (A1, A2, A3, A4, A5))._2)(a => t => t.copy(_2 = a))
  }

  implicit def tuple5Field3[A1, A2, A3, A4, A5]: Field3[(A1, A2, A3, A4, A5), A3] = new Field3[(A1, A2, A3, A4, A5), A3] {
    def third = Lens((_: (A1, A2, A3, A4, A5))._3)(a => t => t.copy(_3 = a))
  }

  implicit def tuple5Field4[A1, A2, A3, A4, A5]: Field4[(A1, A2, A3, A4, A5), A4] = new Field4[(A1, A2, A3, A4, A5), A4] {
    def fourth = Lens((_: (A1, A2, A3, A4, A5))._4)(a => t => t.copy(_4 = a))
  }

  implicit def tuple5Field5[A1, A2, A3, A4, A5]: Field5[(A1, A2, A3, A4, A5), A5] = new Field5[(A1, A2, A3, A4, A5), A5] {
    def fifth = Lens((_: (A1, A2, A3, A4, A5))._5)(a => t => t.copy(_5 = a))
  }

  implicit def tuple5Cons1[A1, A2, A3, A4, A5]: Cons1[(A1, A2, A3, A4, A5), A1, (A2, A3, A4, A5)] = new Cons1[(A1, A2, A3, A4, A5), A1, (A2, A3, A4, A5)]{
    def cons1 = Iso[(A1, A2, A3, A4, A5), (A1, (A2, A3, A4, A5))](t => (t._1, (t._2, t._3, t._4, t._5))){ case (h, t) => (h, t._1, t._2, t._3, t._4) }
  }

  implicit def tuple5Snoc1[A1, A2, A3, A4, A5]: Snoc1[(A1, A2, A3, A4, A5), (A1, A2, A3, A4), A5] = new Snoc1[(A1, A2, A3, A4, A5), (A1, A2, A3, A4), A5]{
    def snoc1 = Iso[(A1, A2, A3, A4, A5), ((A1, A2, A3, A4), A5)](t => ((t._1, t._2, t._3, t._4), t._5)){ case (i, l) => (i._1, i._2, i._3, i._4, l) }
  }

  implicit def tuple5Reverse[A, B, C, D, E]: Reverse[(A, B, C, D, E), (E, D, C, B, A)] = new Reverse[(A, B, C, D, E), (E, D, C, B, A)] {
    def reverse = Iso{t: (A, B, C, D, E) => (t._5, t._4, t._3, t._2, t._1)}(t => (t._5, t._4, t._3, t._2, t._1))
  }

}
