package monocle.function

import monocle.Iso

trait Reverse[From] {
  type To

  def reverse: Iso[From, To]
}

object Reverse {
  type Aux[From, _To] = Reverse[From] { type To = _To }

  type AuxId[From] = Reverse[From] { type To = From }

  def apply[From, _To](iso: Iso[From, _To]): Aux[From, _To] =
    new Reverse[From] {
      type To = _To
      def reverse: Iso[From, _To] = iso
    }

  implicit def listReverse[A]: AuxId[List[A]] =
    apply(Iso[List[A], List[A]](_.reverse)(_.reverse))

  implicit def vectorReverse[A]: AuxId[Vector[A]] =
    apply(Iso[Vector[A], Vector[A]](_.reverse)(_.reverse))

  implicit def stringReverse: AuxId[String] =
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
    Iso[(A1, A2, A3, A4, A5), (A5, A4, A3, A2, A1)](a => (a._5, a._4, a._3, a._2, a._1))(
      a => (a._5, a._4, a._3, a._2, a._1)
    )
  )

  implicit def tuple6Reverse[A1, A2, A3, A4, A5, A6]: Reverse[(A1, A2, A3, A4, A5, A6)] = Reverse(
    Iso[(A1, A2, A3, A4, A5, A6), (A6, A5, A4, A3, A2, A1)](a => (a._6, a._5, a._4, a._3, a._2, a._1))(
      a => (a._6, a._5, a._4, a._3, a._2, a._1)
    )
  )
}
