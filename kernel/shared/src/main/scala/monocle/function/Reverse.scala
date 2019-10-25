package monocle.function

import monocle.Iso

trait Reverse[A] {
  type B

  def reverse: Iso[A, B]
}

object Reverse {
  type Aux[A, B0] = Reverse[A] { type B = B0 }

  def apply[A, B0](iso: Iso[A, B0]): Aux[A, B0] =
    new Reverse[A] {
      type B = B0
      def reverse: Iso[A, B0] = iso
    }

  implicit def listReverse[A]: Aux[List[A], List[A]] =
    apply(Iso[List[A], List[A]](_.reverse)(_.reverse))

  implicit def vectorReverse[A]: Aux[Vector[A], Vector[A]] =
    apply(Iso[Vector[A], Vector[A]](_.reverse)(_.reverse))

  implicit def stringReverse: Aux[String, String] =
    apply(Iso[String, String](_.reverse)(_.reverse))

  implicit def tuple2Reverse[A1, A2]: Reverse[(A1, A2)] = Reverse(
    Iso[(A1, A2), (A2, A1)](_.swap)(_.swap)
  )

  implicit def tuple3Reverse[A1, A2, A3]: Reverse[(A1, A2, A3)] = Reverse(
    Iso[(A1, A2, A3), (A3, A2, A1)](a => (a._3, a._2, a._1))(a => (a._3, a._2, a._1))
  )

  implicit def tuple4Reverse[A1, A2, A3, A4]: Reverse[(A1, A2, A3, A4)] = Reverse(
    Iso[(A1, A2, A3, A4), (A4, A3, A2, A1)](a => (a._4, a._3, a._2, a._1))(a => (a._4, a._3, a._2, a._1))
  )

  implicit def tuple5Reverse[A1, A2, A3, A4, A5]: Reverse[(A1, A2, A3, A4, A5)] = Reverse(
    Iso[(A1, A2, A3, A4, A5), (A5, A4, A3, A2, A1)](a => (a._5, a._4, a._3, a._2, a._1))(a => (a._5, a._4, a._3, a._2, a._1))
  )

  implicit def tuple6Reverse[A1, A2, A3, A4, A5, A6]: Reverse[(A1, A2, A3, A4, A5, A6)] = Reverse(
    Iso[(A1, A2, A3, A4, A5, A6), (A6, A5, A4, A3, A2, A1)](a => (a._6, a._5, a._4, a._3, a._2, a._1))(a => (a._6, a._5, a._4, a._3, a._2, a._1))
  )
}
