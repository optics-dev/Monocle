package monocle.function

import monocle.Lens

trait Field1[S] {
  type A
  def first: Lens[S, A]
}

object Field1 {
  type Aux[S, A0] = Field1[S] { type A = A0 }

  def apply[S, A0](lens: Lens[S, A0]): Aux[S, A0] = new Field1[S] {
    type A = A0
    override val first: Lens[S, A] = lens
  }

  implicit def tuple2[A1, A2]: Aux[(A1, A2), A1] =
    apply(Lens[(A1, A2), A1](_._1){ case ((_, a2), a1) => (a1, a2) })
}


trait Field2[S] {
  type A
  def second: Lens[S, A]
}

object Field2 {
  type Aux[S, A0] = Field2[S] { type A = A0 }

  def apply[S, A0](lens: Lens[S, A0]): Aux[S, A0] = new Field2[S] {
    type A = A0
    override val second: Lens[S, A] = lens
  }

  implicit def tuple2[A1, A2]: Aux[(A1, A2), A2] =
    apply(Lens[(A1, A2), A2](_._2){ case ((a1, _), a2) => (a1, a2) })
}