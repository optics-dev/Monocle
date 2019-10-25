package monocle.function

import monocle.Lens

trait Field1[A] {
  type B
  def first: Lens[A, B]
}

object Field1 {
  type Aux[A, B0] = Field1[A] { type B = B0 }

  def apply[A, B0](lens: Lens[A, B0]): Aux[A, B0] = new Field1[A] {
    type B = B0
    override val first: Lens[A, B0] = lens
  }

  implicit def tuple2[A1, A2]: Aux[(A1, A2), A1] =
    apply(Lens[(A1, A2), A1](_._1) { case ((_, a2), a1) => (a1, a2) })

  implicit def tuple3[A1, A2, A3]: Aux[(A1, A2, A3), A1] =
    apply(Lens[(A1, A2, A3), A1](_._1) {
      case ((_, a2, a3), a1) => (a1, a2, a3)
    })

  implicit def tuple4[A1, A2, A3, A4]: Aux[(A1, A2, A3, A4), A1] =
    apply(Lens[(A1, A2, A3, A4), A1](_._1) {
      case ((_, a2, a3, a4), a1) => (a1, a2, a3, a4)
    })

  implicit def tuple5[A1, A2, A3, A4, A5]: Aux[(A1, A2, A3, A4, A5), A1] =
    apply(Lens[(A1, A2, A3, A4, A5), A1](_._1) {
      case ((_, a2, a3, a4, a5), a1) => (a1, a2, a3, a4, a5)
    })

  implicit def tuple6[A1, A2, A3, A4, A5, A6]: Aux[(A1, A2, A3, A4, A5, A6), A1] =
    apply(Lens[(A1, A2, A3, A4, A5, A6), A1](_._1) {
      case ((_, a2, a3, a4, a5, a6), a1) => (a1, a2, a3, a4, a5, a6)
    })
}

trait Field2[A] {
  type B
  def second: Lens[A, B]
}

object Field2 {
  type Aux[A, B0] = Field2[A] { type B = B0 }

  def apply[A, B0](lens: Lens[A, B0]): Aux[A, B0] = new Field2[A] {
    type B = B0
    override val second: Lens[A, B0] = lens
  }

  implicit def tuple2[A1, A2]: Aux[(A1, A2), A2] =
    apply(Lens[(A1, A2), A2](_._2) { case ((a1, _), a2) => (a1, a2) })

  implicit def tuple3[A1, A2, A3]: Aux[(A1, A2, A3), A2] =
    apply(Lens[(A1, A2, A3), A2](_._2) {
      case ((a1, _, a3), a2) => (a1, a2, a3)
    })

  implicit def tuple4[A1, A2, A3, A4]: Aux[(A1, A2, A3, A4), A2] =
    apply(Lens[(A1, A2, A3, A4), A2](_._2) {
      case ((a1, _, a3, a4), a2) => (a1, a2, a3, a4)
    })

  implicit def tuple5[A1, A2, A3, A4, A5]: Aux[(A1, A2, A3, A4, A5), A2] =
    apply(Lens[(A1, A2, A3, A4, A5), A2](_._2) {
      case ((a1, _, a3, a4, a5), a2) => (a1, a2, a3, a4, a5)
    })

  implicit def tuple6[A1, A2, A3, A4, A5, A6]: Aux[(A1, A2, A3, A4, A5, A6), A2] =
    apply(Lens[(A1, A2, A3, A4, A5, A6), A2](_._2) {
      case ((a1, _, a3, a4, a5, a6), a2) => (a1, a2, a3, a4, a5, a6)
    })
}

trait Field3[A] {
  type B
  def third: Lens[A, B]
}

object Field3 {
  type Aux[A, B0] = Field3[A] { type B = B0 }

  def apply[A, B0](lens: Lens[A, B0]): Aux[A, B0] = new Field3[A] {
    type B = B0
    override val third: Lens[A, B] = lens
  }

  implicit def tuple3[A1, A2, A3]: Aux[(A1, A2, A3), A3] =
    apply(Lens[(A1, A2, A3), A3](_._3) {
      case ((a1, a2, _), a3) => (a1, a2, a3)
    })

  implicit def tuple4[A1, A2, A3, A4]: Aux[(A1, A2, A3, A4), A3] =
    apply(Lens[(A1, A2, A3, A4), A3](_._3) {
      case ((a1, a2, _, a4), a3) => (a1, a2, a3, a4)
    })

  implicit def tuple5[A1, A2, A3, A4, A5]: Aux[(A1, A2, A3, A4, A5), A3] =
    apply(Lens[(A1, A2, A3, A4, A5), A3](_._3) {
      case ((a1, a2, _, a4, a5), a3) => (a1, a2, a3, a4, a5)
    })

  implicit def tuple6[A1, A2, A3, A4, A5, A6]: Aux[(A1, A2, A3, A4, A5, A6), A3] =
    apply(Lens[(A1, A2, A3, A4, A5, A6), A3](_._3) {
      case ((a1, a2, _, a4, a5, a6), a3) => (a1, a2, a3, a4, a5, a6)
    })
}

trait Field4[A] {
  type B
  def fourth: Lens[A, B]
}

object Field4 {
  type Aux[A, B0] = Field4[A] { type B = B0 }

  def apply[A, B0](lens: Lens[A, B0]): Aux[A, B0] = new Field4[A] {
    type B = B0
    override val fourth: Lens[A, B] = lens
  }

  implicit def tuple4[A1, A2, A3, A4]: Aux[(A1, A2, A3, A4), A4] =
    apply(Lens[(A1, A2, A3, A4), A4](_._4) {
      case ((a1, a2, a3, _), a4) => (a1, a2, a3, a4)
    })

  implicit def tuple5[A1, A2, A3, A4, A5]: Aux[(A1, A2, A3, A4, A5), A4] =
    apply(Lens[(A1, A2, A3, A4, A5), A4](_._4) {
      case ((a1, a2, a3, _, a5), a4) => (a1, a2, a3, a4, a5)
    })

  implicit def tuple6[A1, A2, A3, A4, A5, A6]: Aux[(A1, A2, A3, A4, A5, A6), A4] =
    apply(Lens[(A1, A2, A3, A4, A5, A6), A4](_._4) {
      case ((a1, a2, a3, _, a5, a6), a4) => (a1, a2, a3, a4, a5, a6)
    })
}

trait Field5[A] {
  type B
  def fifth: Lens[A, B]
}

object Field5 {
  type Aux[A, B0] = Field5[A] { type B = B0 }

  def apply[A, B0](lens: Lens[A, B0]): Aux[A, B0] = new Field5[A] {
    type B = B0
    override val fifth: Lens[A, B] = lens
  }

  implicit def tuple5[A1, A2, A3, A4, A5]: Aux[(A1, A2, A3, A4, A5), A5] =
    apply(Lens[(A1, A2, A3, A4, A5), A5](_._5) {
      case ((a1, a2, a3, a4, _), a5) => (a1, a2, a3, a4, a5)
    })

  implicit def tuple6[A1, A2, A3, A4, A5, A6]: Aux[(A1, A2, A3, A4, A5, A6), A5] =
    apply(Lens[(A1, A2, A3, A4, A5, A6), A5](_._5) {
      case ((a1, a2, a3, a4, _, a6), a5) => (a1, a2, a3, a4, a5, a6)
    })
}

trait Field6[A] {
  type B
  def sixth: Lens[A, B]
}

object Field6 {
  type Aux[A, B0] = Field6[A] { type B = B0 }

  def apply[A, B0](lens: Lens[A, B0]): Aux[A, B0] = new Field6[A] {
    type B = B0
    override val sixth: Lens[A, B] = lens
  }

  implicit def tuple6[A1, A2, A3, A4, A5, A6]: Aux[(A1, A2, A3, A4, A5, A6), A6] =
    apply(Lens[(A1, A2, A3, A4, A5, A6), A6](_._6) {
      case ((a1, a2, a3, a4, a5, _), a6) => (a1, a2, a3, a4, a5, a6)
    })
}
