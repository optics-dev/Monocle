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
}
