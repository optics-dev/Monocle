package monocle.function

import monocle.{Lens, Optional, Prism}

trait Cons[From] {
  type Head

  def cons: Prism[From, (Head, From)]

  def headOption: Optional[From, Head] = cons composeLens Lens.first
  def tailOption: Optional[From, From] = cons composeLens Lens.second
}

object Cons {
  type Aux[From, _Head] = Cons[From] { type Head = _Head }

  def apply[From, _Head](prism: Prism[From, (_Head, From)]): Aux[From, _Head] =
    new Cons[From] {
      type Head = _Head
      def cons: Prism[From, (Head, From)] = prism
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
