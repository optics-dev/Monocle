package monocle.function

import monocle.{Optional, Prism}

trait Possible[A] {
  type B

  def possible: Optional[A, B]
}

object Possible {
  type Aux[A, B0] = Possible[A] { type B = B0 }

  def apply[A, B0](optional: Optional[A, B0]): Aux[A, B0] = new Possible[A] {
    type B = B0
    override val possible: Optional[A, B0] = optional
  }

  implicit def optionPossible[A]: Aux[Option[A], A] =
    apply(Prism.some)
}
