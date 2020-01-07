package monocle.function

import monocle.{Optional, Prism}

trait Possible[From] {
  type To

  def possible: Optional[From, To]
}

object Possible {
  type Aux[From, _To] = Possible[From] { type To = _To }

  def apply[From, _To](optional: Optional[From, _To]): Aux[From, _To] = new Possible[From] {
    type To = _To
    val possible: Optional[From, _To] = optional
  }

  implicit def optionPossible[A]: Aux[Option[A], A] =
    apply(Prism.some)
}
