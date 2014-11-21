package monocle.std

import monocle.function._
import monocle.{Iso, Lens, PTraversal}

object tuple6 extends Tuple6Instances

trait Tuple6Instances {

  implicit def tuple6Each[A]: Each[(A, A, A, A, A, A), A] = new Each[(A, A, A, A, A, A), A] {
    def each =
      PTraversal.apply6[(A, A, A, A, A, A), (A, A, A, A, A, A), A, A](_._1,_._2,_._3,_._4,_._5, _._6)((b1, b2, b3, b4, b5, b6, _) => (b1, b2, b3, b4, b5, b6))
  }

  implicit def tuple6Field1[A1, A2, A3, A4, A5, A6]: Field1[(A1, A2, A3, A4, A5, A6), A1] = new Field1[(A1, A2, A3, A4, A5, A6), A1] {
    def first = Lens((_: (A1, A2, A3, A4, A5, A6))._1)(a => t => t.copy(_1 = a))
  }

  implicit def tuple6Field2[A1, A2, A3, A4, A5, A6]: Field2[(A1, A2, A3, A4, A5, A6), A2] = new Field2[(A1, A2, A3, A4, A5, A6), A2] {
    def second = Lens((_: (A1, A2, A3, A4, A5, A6))._2)(a => t => t.copy(_2 = a))
  }

  implicit def tuple6Field3[A1, A2, A3, A4, A5, A6]: Field3[(A1, A2, A3, A4, A5, A6), A3] = new Field3[(A1, A2, A3, A4, A5, A6), A3] {
    def third = Lens((_: (A1, A2, A3, A4, A5, A6))._3)(a => t => t.copy(_3 = a))
  }

  implicit def tuple6Field4[A1, A2, A3, A4, A5, A6]: Field4[(A1, A2, A3, A4, A5, A6), A4] = new Field4[(A1, A2, A3, A4, A5, A6), A4] {
    def fourth = Lens((_: (A1, A2, A3, A4, A5, A6))._4)(a => t => t.copy(_4 = a))
  }

  implicit def tuple6Field5[A1, A2, A3, A4, A5, A6]: Field5[(A1, A2, A3, A4, A5, A6), A5] = new Field5[(A1, A2, A3, A4, A5, A6), A5] {
    def fifth = Lens((_: (A1, A2, A3, A4, A5, A6))._5)(a => t => t.copy(_5 = a))
  }

  implicit def tuple6Field6[A1, A2, A3, A4, A5, A6]: Field6[(A1, A2, A3, A4, A5, A6), A6] = new Field6[(A1, A2, A3, A4, A5, A6), A6] {
    def sixth = Lens((_: (A1, A2, A3, A4, A5, A6))._6)(a => t => t.copy(_6 = a))
  }

  implicit def tuple6Cons1[A1, A2, A3, A4, A5, A6]: Cons1[(A1, A2, A3, A4, A5, A6), A1, (A2, A3, A4, A5, A6)] = new Cons1[(A1, A2, A3, A4, A5, A6), A1, (A2, A3, A4, A5, A6)]{
    def cons1 = Iso[(A1, A2, A3, A4, A5, A6), (A1, (A2, A3, A4, A5, A6))](t => (t._1, (t._2, t._3, t._4, t._5, t._6))){ case (h, t) => (h, t._1, t._2, t._3, t._4, t._5) }
  }

  implicit def tuple6Snoc1[A1, A2, A3, A4, A5, A6]: Snoc1[(A1, A2, A3, A4, A5, A6), (A1, A2, A3, A4, A5), A6] = new Snoc1[(A1, A2, A3, A4, A5, A6), (A1, A2, A3, A4, A5), A6]{
    def snoc1 = Iso[(A1, A2, A3, A4, A5, A6), ((A1, A2, A3, A4, A5), A6)](t => ((t._1, t._2, t._3, t._4, t._5), t._6)){ case (i, l) => (i._1, i._2, i._3, i._4, i._5, l) }
  }

  implicit def tuple6Reverse[A, B, C, D, E, F]: Reverse[(A, B, C, D, E, F), (F, E, D, C, B, A)] = new Reverse[(A, B, C, D, E, F), (F, E, D, C, B, A)] {
    def reverse = Iso{t: (A, B, C, D, E, F) => (t._6, t._5, t._4, t._3, t._2, t._1)}(t => (t._6, t._5, t._4, t._3, t._2, t._1))
  }
}
