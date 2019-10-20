package monocle.function

import monocle.Optional

trait Possible[S] {
  type A
  def possible: Optional[S, A]
}

object Possible {
  type Aux[S, A0] = Possible[S] { type A = A0 }

  def apply[S, A0](optional: Optional[S, A0]): Aux[S, A0] = new Possible[S] {
    type A = A0
    override val possible: Optional[S, A0] = optional
  }

  implicit def optionPossible[A0](implicit ev: Optional[Option[A0], A0]): Possible[Option[A0]] =
    new Possible[Option[A0]] {
      type A = A0
      def possible = ev
    }

}
