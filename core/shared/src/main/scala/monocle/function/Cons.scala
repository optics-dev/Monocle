package monocle.function

import monocle.{Lens, Optional, Prism}

trait Cons[A] {
  type B

  def cons: Prism[A, (B, A)]

  def headOption: Optional[A, B] = cons composeLens Lens.first
  def tailOption: Optional[A, A] = cons composeLens Lens.second
}

object Cons {
  type Aux[A, B0] = Cons[A] { type B = B0 }

  def apply[A, B0](prism: Prism[A, (B0, A)]): Aux[A, B0] =
    new Cons[A] {
      type B = B0
      def cons: Prism[A, (B0, A)] = prism
    }

  implicit def list[A]: Cons.Aux[List[A], A] =
    apply(Prism[List[A], (A, List[A])] {
      case Nil     => None
      case x :: xs => Some((x, xs))
    } { case (x, xs) => x :: xs })

  implicit def vector[A]: Cons.Aux[Vector[A], A] =
    apply(Prism[Vector[A], (A, Vector[A])](xs => xs.headOption.map(_ -> xs.tail)) {
      case (x, xs) => x +: xs
    })
}
