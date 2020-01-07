package monocle.function

import monocle.Lens

trait Field1[From] {
  type To
  def first: Lens[From, To]
}

object Field1 {
  type Aux[A, _To] = Field1[A] { type To = _To }

  def apply[From, _To](lens: Lens[From, _To]): Aux[From, _To] = new Field1[From] {
    type To = _To
    override val first: Lens[From, _To] = lens
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

trait Field2[From] {
  type To
  def second: Lens[From, To]
}

object Field2 {
  type Aux[A, _To] = Field2[A] { type To = _To }

  def apply[From, _To](lens: Lens[From, _To]): Aux[From, _To] = new Field2[From] {
    type To = _To
    override val second: Lens[From, _To] = lens
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

trait Field3[From] {
  type To
  def third: Lens[From, To]
}

object Field3 {
  type Aux[A, _To] = Field3[A] { type To = _To }

  def apply[From, _To](lens: Lens[From, _To]): Aux[From, _To] = new Field3[From] {
    type To = _To
    override val third: Lens[From, To] = lens
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

trait Field4[From] {
  type To
  def fourth: Lens[From, To]
}

object Field4 {
  type Aux[A, _To] = Field4[A] { type To = _To }

  def apply[From, _To](lens: Lens[From, _To]): Aux[From, _To] = new Field4[From] {
    type To = _To
    override val fourth: Lens[From, To] = lens
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

trait Field5[From] {
  type To
  def fifth: Lens[From, To]
}

object Field5 {
  type Aux[A, _To] = Field5[A] { type To = _To }

  def apply[From, _To](lens: Lens[From, _To]): Aux[From, _To] = new Field5[From] {
    type To = _To
    override val fifth: Lens[From, To] = lens
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

trait Field6[From] {
  type To
  def sixth: Lens[From, To]
}

object Field6 {
  type Aux[A, _To] = Field6[A] { type To = _To }

  def apply[From, _To](lens: Lens[From, _To]): Aux[From, _To] = new Field6[From] {
    type To = _To
    override val sixth: Lens[From, To] = lens
  }

  implicit def tuple6[A1, A2, A3, A4, A5, A6]: Aux[(A1, A2, A3, A4, A5, A6), A6] =
    apply(Lens[(A1, A2, A3, A4, A5, A6), A6](_._6) {
      case ((a1, a2, a3, a4, a5, _), a6) => (a1, a2, a3, a4, a5, a6)
    })
}
