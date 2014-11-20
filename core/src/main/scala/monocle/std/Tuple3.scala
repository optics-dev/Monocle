package monocle.std

import monocle.{Iso, Lens, PTraversal}
import monocle.function._

object tuple3 extends Tuple3Instances

trait Tuple3Instances {

  implicit def tuple3Each[A]: Each[(A, A, A), A] = new Each[(A, A, A), A] {
    def each =
      PTraversal.apply3[(A, A, A), (A, A, A), A, A](_._1,_._2,_._3)((b1, b2, b3, _) => (b1, b2, b3))
  }

  implicit def tuple3Field1[A1, A2, A3]: Field1[(A1, A2, A3), A1] = new Field1[(A1, A2, A3), A1] {
    def first = Lens((_: (A1, A2, A3))._1)( (a, t) => t.copy(_1 = a))
  }

  implicit def tuple3Field2[A1, A2, A3]: Field2[(A1, A2, A3), A2] = new Field2[(A1, A2, A3), A2] {
    def second = Lens((_: (A1, A2, A3))._2)( (a, t) => t.copy(_2 = a))
  }

  implicit def tuple3Field3[A1, A2, A3]: Field3[(A1, A2, A3), A3] = new Field3[(A1, A2, A3), A3] {
    def third = Lens((_: (A1, A2, A3))._3)( (a, t) => t.copy(_3 = a))
  }

  implicit def tuple3Cons1[A1, A2, A3]: Cons1[(A1, A2, A3), A1, (A2, A3)] = new Cons1[(A1, A2, A3), A1, (A2, A3)] {
    def cons1 = Iso[(A1, A2, A3), (A1, (A2, A3))](t => (t._1, (t._2, t._3))){ case (h, t) => (h, t._1, t._2) }
  }

  implicit def tuple3Snoc1[A1, A2, A3]: Snoc1[(A1, A2, A3), (A1, A2), A3] = new Snoc1[(A1, A2, A3), (A1, A2), A3]{
    def snoc1 = Iso[(A1, A2, A3), ((A1, A2), A3)](t => ((t._1, t._2), t._3)){ case (i, l) => (i._1, i._2, l) }
  }

  implicit def tuple3Reverse[A, B, C]: Reverse[(A, B, C), (C, B, A)] = new Reverse[(A, B, C), (C, B, A)] {
    def reverse = Iso{t: (A, B, C) => (t._3, t._2, t._1)}(t => (t._3, t._2, t._1))
  }

}
