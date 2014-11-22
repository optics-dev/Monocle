package monocle.std

import monocle.function._
import monocle.{Iso, Lens, PTraversal}

object tuple2 extends Tuple2Instances

trait Tuple2Instances {

  implicit def tuple2Each[A]: Each[(A, A), A] = new Each[(A, A), A] {
    def each =
      PTraversal.apply2[(A, A), (A, A), A, A](_._1,_._2)((b1, b2, _) => (b1, b2))
  }

  implicit def tuple2Field1[A1, A2]: Field1[(A1, A2), A1] = new Field1[(A1, A2), A1] {
    def first = Lens((_: (A1, A2))._1)(a => t => t.copy(_1 = a))
  }

  implicit def tuple2Field2[A1, A2]: Field2[(A1, A2), A2]  = new Field2[(A1, A2), A2] {
    def second = Lens((_: (A1, A2))._2)(a => t => t.copy(_2 = a))
  }

  implicit def tuple2Cons1[A1, A2]: Cons1[(A1, A2), A1, A2] = new Cons1[(A1, A2), A1, A2] {
    def cons1 = Iso[(A1, A2), (A1, A2)](identity)(identity)
  }

  implicit def tuple2Snoc1[A1, A2]: Snoc1[(A1, A2), A1, A2] = new Snoc1[(A1, A2), A1, A2] {
    def snoc1 = Iso[(A1, A2), (A1, A2)](identity)(identity)
  }

  implicit def tuple2Reverse[A, B]: Reverse[(A, B), (B, A)] = new Reverse[(A, B), (B, A)] {
    def reverse = Iso[(A, B), (B, A)](_.swap)(_.swap)
  }

}
