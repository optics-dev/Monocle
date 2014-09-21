package monocle.std

import monocle.{SimpleIso, SimpleLens, Traversal}
import monocle.function._

object tuple3 extends Tuple3Instances

trait Tuple3Instances {

  implicit def tuple3Each[A]: Each[(A, A, A), A] = new Each[(A, A, A), A] {
    def each =
      Traversal.apply3[(A, A, A), (A, A, A), A, A](_._1,_._2,_._3)((b1, b2, b3, _) => (b1, b2, b3))
  }

  implicit def tuple3Field1[A1, A2, A3]: Field1[(A1, A2, A3), A1] = new Field1[(A1, A2, A3), A1] {
    def first = SimpleLens[(A1, A2, A3), A1](_._1, (a, t) => t.copy(_1 = a))
  }

  implicit def tuple3Field2[A1, A2, A3]: Field2[(A1, A2, A3), A2] = new Field2[(A1, A2, A3), A2] {
    def second = SimpleLens[(A1, A2, A3), A2](_._2, (a, t) => t.copy(_2 = a))
  }

  implicit def tuple3Field3[A1, A2, A3]: Field3[(A1, A2, A3), A3] = new Field3[(A1, A2, A3), A3] {
    def third = SimpleLens[(A1, A2, A3), A3](_._3, (a, t) => t.copy(_3 = a))
  }

  implicit def tuple3Head[A1, A2, A3]: Head[(A1, A2, A3), A1] =
    Head.field1Head[(A1, A2, A3), A1]

  implicit def tuple3Tail[A1, A2, A3]: Tail[(A1, A2, A3), (A2, A3)] = new Tail[(A1, A2, A3), (A2, A3)] {
    def tail = SimpleLens[(A1, A2, A3), (A2, A3)](t => (t._2, t._3), (a, t) => t.copy(_2 = a._1, _3 = a._2))
  }

  implicit def tuple3Last[A1, A2, A3]: Last[(A1, A2, A3), A3] = new Last[(A1, A2, A3), A3] {
    def last = third
  }

  implicit def tuple3Init[A1, A2, A3]: Init[(A1, A2, A3), (A1, A2)] = new Init[(A1, A2, A3), (A1, A2)] {
    def init = SimpleLens[(A1, A2, A3), (A1, A2)](t => (t._1, t._2), (a, t) => t.copy(_1 = a._1, _2 = a._2))
  }

  implicit def tuple3Reverse[A, B, C]: Reverse[(A, B, C), (C, B, A)] = new Reverse[(A, B, C), (C, B, A)] {
    def reverse = SimpleIso[(A, B, C), (C, B, A)](t => (t._3, t._2, t._1), t => (t._3, t._2, t._1))
  }

}
