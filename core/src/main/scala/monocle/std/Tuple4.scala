package monocle.std

import monocle.{Iso, Lens, PTraversal}
import monocle.function._

object tuple4 extends Tuple4Instances

trait Tuple4Instances {

  implicit def tuple4Each[A]: Each[(A, A, A, A), A] = new Each[(A, A, A, A), A] {
    def each =
      PTraversal.apply4[(A, A, A, A), (A, A, A, A), A, A](_._1,_._2,_._3,_._4)((b1, b2, b3, b4, _) => (b1, b2, b3, b4))
  }

  implicit def tuple4Field1[A1, A2, A3, A4]: Field1[(A1, A2, A3, A4), A1] = new Field1[(A1, A2, A3, A4), A1] {
    def first = Lens((_: (A1, A2, A3, A4))._1)(a => t => t.copy(_1 = a))
  }

  implicit def tuple4Field2[A1, A2, A3, A4]: Field2[(A1, A2, A3, A4), A2] = new Field2[(A1, A2, A3, A4), A2] {
    def second = Lens((_: (A1, A2, A3, A4))._2)(a => t => t.copy(_2 = a))
  }

  implicit def tuple4Field3[A1, A2, A3, A4]: Field3[(A1, A2, A3, A4), A3]  = new Field3[(A1, A2, A3, A4), A3] {
    def third = Lens((_: (A1, A2, A3, A4))._3)(a => t => t.copy(_3 = a))
  }

  implicit def tuple4Field4[A1, A2, A3, A4]: Field4[(A1, A2, A3, A4), A4] = new Field4[(A1, A2, A3, A4), A4] {
    def fourth = Lens((_: (A1, A2, A3, A4))._4)(a => t => t.copy(_4 = a))
  }

  implicit def tuple4Cons1[A1, A2, A3, A4]: Cons1[(A1, A2, A3, A4), A1, (A2, A3, A4)] = new Cons1[(A1, A2, A3, A4), A1, (A2, A3, A4)]{
    def cons1 = Iso[(A1, A2, A3, A4), (A1, (A2, A3, A4))](t => (t._1, (t._2, t._3, t._4))){ case (h, t) => (h, t._1, t._2, t._3) }
  }

  implicit def tuple4Snoc1[A1, A2, A3, A4]: Snoc1[(A1, A2, A3, A4), (A1, A2, A3), A4] = new Snoc1[(A1, A2, A3, A4), (A1, A2, A3), A4]{
    def snoc1 = Iso[(A1, A2, A3, A4), ((A1, A2, A3), A4)](t => ((t._1, t._2, t._3), t._4)){ case (i, l) => (i._1, i._2, i._3, l) }
  }

  implicit def tuple4Reverse[A, B, C, D]: Reverse[(A, B, C, D), (D, C, B, A)] = new Reverse[(A, B, C, D), (D, C, B, A)] {
    def reverse = Iso{t: (A, B, C, D) => (t._4, t._3, t._2, t._1)}(t => (t._4, t._3, t._2, t._1))
  }

}
