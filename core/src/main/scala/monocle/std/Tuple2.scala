package monocle.std

import monocle.function._
import monocle.{SimpleIso, SimpleLens, Traversal}

object tuple2 extends Tuple2Instances

trait Tuple2Instances {

  implicit def tuple2Each[A]: Each[(A, A), A] = new Each[(A, A), A] {
    def each =
      Traversal.apply2[(A, A), (A, A), A, A](_._1,_._2)((_, b1, b2) => (b1, b2))
  }

  implicit def tuple2Field1[A1, A2]: Field1[(A1, A2), A1] = new Field1[(A1, A2), A1] {
    def first = SimpleLens[(A1, A2), A1](_._1, (t, a) => t.copy(_1 = a))
  }

  implicit def tuple2Field2[A1, A2]: Field2[(A1, A2), A2]  = new Field2[(A1, A2), A2] {
    def second = SimpleLens[(A1, A2), A2](_._2, (t, a) => t.copy(_2 = a))
  }

  implicit def tuple2HCons[A1, A2]: HCons[(A1, A2), A1, A2] = new HCons[(A1, A2), A1, A2] {
    def hcons = SimpleIso[(A1, A2), (A1, A2)](identity, identity)
  }

  implicit def tuple2HSnoc[A1, A2]: HSnoc[(A1, A2), A1, A2] = new HSnoc[(A1, A2), A1, A2] {
    def hsnoc = SimpleIso[(A1, A2), (A1, A2)](identity, identity)
  }
  implicit def tuple2Reverse[A, B]: Reverse[(A, B), (B, A)] = new Reverse[(A, B), (B, A)] {
    def reverse = SimpleIso[(A, B), (B, A)](_.swap, _.swap)
  }

}
