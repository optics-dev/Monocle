package monocle.std

import monocle.function._
import monocle.{SimpleIso, SimpleLens, Traversal}

object tuple5 extends Tuple5Instances

trait Tuple5Instances {

  implicit def tuple5Each[A]: Each[(A, A, A, A, A), A] = new Each[(A, A, A, A, A), A] {
    def each =
      Traversal.apply5[(A, A, A, A, A), (A, A, A, A, A), A, A](_._1,_._2,_._3,_._4,_._5)((_, b1, b2, b3, b4, b5) => (b1, b2, b3, b4, b5))
  }

  implicit def tuple5Field1[A1, A2, A3, A4, A5]: Field1[(A1, A2, A3, A4, A5), A1] = new Field1[(A1, A2, A3, A4, A5), A1] {
    def first = SimpleLens[(A1, A2, A3, A4, A5), A1](_._1, (t, a) => t.copy(_1 = a))
  }

  implicit def tuple5Field2[A1, A2, A3, A4, A5]: Field2[(A1, A2, A3, A4, A5), A2] = new Field2[(A1, A2, A3, A4, A5), A2] {
    def second = SimpleLens[(A1, A2, A3, A4, A5), A2](_._2, (t, a) => t.copy(_2 = a))
  }

  implicit def tuple5Field3[A1, A2, A3, A4, A5]: Field3[(A1, A2, A3, A4, A5), A3] = new Field3[(A1, A2, A3, A4, A5), A3] {
    def third = SimpleLens[(A1, A2, A3, A4, A5), A3](_._3, (t, a) => t.copy(_3 = a))
  }

  implicit def tuple5Field4[A1, A2, A3, A4, A5]: Field4[(A1, A2, A3, A4, A5), A4] = new Field4[(A1, A2, A3, A4, A5), A4] {
    def fourth = SimpleLens[(A1, A2, A3, A4, A5), A4](_._4, (t, a) => t.copy(_4 = a))
  }

  implicit def tuple5Field5[A1, A2, A3, A4, A5]: Field5[(A1, A2, A3, A4, A5), A5] = new Field5[(A1, A2, A3, A4, A5), A5] {
    def fifth = SimpleLens[(A1, A2, A3, A4, A5), A5](_._5, (t, a) => t.copy(_5 = a))
  }

  implicit def tuple5HCons[A1, A2, A3, A4, A5]: HCons[(A1, A2, A3, A4, A5), A1, (A2, A3, A4, A5)] = new HCons[(A1, A2, A3, A4, A5), A1, (A2, A3, A4, A5)]{
    def hcons = SimpleIso[(A1, A2, A3, A4, A5), (A1, (A2, A3, A4, A5))](t => (t._1, (t._2, t._3, t._4, t._5)), { case (h, t) => (h, t._1, t._2, t._3, t._4) })
  }

  implicit def tuple5HSnoc[A1, A2, A3, A4, A5]: HSnoc[(A1, A2, A3, A4, A5), (A1, A2, A3, A4), A5] = new HSnoc[(A1, A2, A3, A4, A5), (A1, A2, A3, A4), A5]{
    def hsnoc = SimpleIso[(A1, A2, A3, A4, A5), ((A1, A2, A3, A4), A5)](t => ((t._1, t._2, t._3, t._4), t._5), { case (i, l) => (i._1, i._2, i._3, i._4, l) })
  }

  implicit def tuple5Reverse[A, B, C, D, E]: Reverse[(A, B, C, D, E), (E, D, C, B, A)] = new Reverse[(A, B, C, D, E), (E, D, C, B, A)] {
    def reverse = SimpleIso[(A, B, C, D, E), (E, D, C, B, A)](t => (t._5, t._4, t._3, t._2, t._1), t => (t._5, t._4, t._3, t._2, t._1))
  }

}
