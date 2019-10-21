package monocle.function

import monocle.{Lens, Optional, Prism}


trait Cons[S] {
  type A

  def cons: Prism[S, (A, S)]

  def headOption: Optional[S, A] = cons composeLens Lens.first
  def tailOption: Optional[S, S] = cons composeLens Lens.second
}

object Cons {
  type Aux[S, A0] = Cons[S] { type A = A0 }

  def apply[S, A0](prism: Prism[S, (A0, S)]): Aux[S, A0] =
    new Cons[S] {
      type A = A0
      def cons: Prism[S, (A0, S)] = prism
    }

  implicit def list[A]: Cons[List[A]] =
    apply(Prism[List[A], (A, List[A])]{
      case Nil     => None
      case x :: xs => Some((x, xs))
    }{ case (x, xs) => x :: xs })
}
