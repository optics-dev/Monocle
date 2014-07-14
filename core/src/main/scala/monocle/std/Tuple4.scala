package monocle.std

import monocle.{SimpleIso, SimpleLens, Traversal}
import monocle.function._

object tuple4 extends Tuple4Instances

trait Tuple4Instances {

  implicit def tuple4Each[A]: Each[(A, A, A, A), A] = new Each[(A, A, A, A), A] {
    def each =
      Traversal.apply4[(A, A, A, A), (A, A, A, A), A, A](_._1,_._2,_._3,_._4)((_, b1, b2, b3, b4) => (b1, b2, b3, b4))
  }

  implicit def tuple4Field1[A1, A2, A3, A4]: Field1[(A1, A2, A3, A4), A1] = new Field1[(A1, A2, A3, A4), A1] {
    def first = SimpleLens[(A1, A2, A3, A4), A1](_._1, (t, a) => t.copy(_1 = a))
  }

  implicit def tuple4Field2[A1, A2, A3, A4]: Field2[(A1, A2, A3, A4), A2] = new Field2[(A1, A2, A3, A4), A2] {
    def second = SimpleLens[(A1, A2, A3, A4), A2](_._2, (t, a) => t.copy(_2 = a))
  }

  implicit def tuple4Field3[A1, A2, A3, A4]: Field3[(A1, A2, A3, A4), A3]  = new Field3[(A1, A2, A3, A4), A3] {
    def third = SimpleLens[(A1, A2, A3, A4), A3](_._3, (t, a) => t.copy(_3 = a))
  }

  implicit def tuple4Field4[A1, A2, A3, A4]: Field4[(A1, A2, A3, A4), A4] = new Field4[(A1, A2, A3, A4), A4] {
    def fourth = SimpleLens[(A1, A2, A3, A4), A4](_._4, (t, a) => t.copy(_4 = a))
  }

  implicit def tuple4Head[A1, A2, A3, A4]: Head[(A1, A2, A3, A4), A1] =
    Head.field1Head[(A1, A2, A3, A4), A1]

  implicit def tuple4Tail[A1, A2, A3, A4]: Tail[(A1, A2, A3, A4), (A2, A3, A4)] = new Tail[(A1, A2, A3, A4), (A2, A3, A4)] {
    def tail = SimpleLens[(A1, A2, A3, A4), (A2, A3, A4)](t => (t._2, t._3, t._4), (t, a) => t.copy(_2 = a._1, _3 = a._2, _4 = a._3))
  }

  implicit def tuple4Last[A1, A2, A3, A4]: Last[(A1, A2, A3, A4), A4] = new Last[(A1, A2, A3, A4), A4] {
    def last = fourth
  }

  implicit def tuple4Init[A1, A2, A3, A4]: Init[(A1, A2, A3, A4), (A1, A2, A3)] = new Init[(A1, A2, A3, A4), (A1, A2, A3)] {
    def init = SimpleLens[(A1, A2, A3, A4), (A1, A2, A3)](t => (t._1, t._2, t._3), (t, a) => t.copy(_1 = a._1, _2 = a._2, _3 = a._3))
  }

  implicit def tuple4Reverse[A, B, C, D]: Reverse[(A, B, C, D), (D, C, B, A)] = new Reverse[(A, B, C, D), (D, C, B, A)] {
    def reverse = SimpleIso[(A, B, C, D), (D, C, B, A)](t => (t._4, t._3, t._2, t._1), t => (t._4, t._3, t._2, t._1))
  }

}
