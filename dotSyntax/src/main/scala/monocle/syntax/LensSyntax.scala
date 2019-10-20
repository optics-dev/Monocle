package monocle.syntax

import monocle.{Lens, Optional}
import monocle.function.{At, Cons, Field1, Field2, Index}

object lens extends LensSyntax

trait LensSyntax {
  implicit class LensOps[S, A](optic: Lens[S, A]) {
    def _1(implicit ev: Field1[A]): Lens[S, ev.A] = first(ev)
    def _2(implicit ev: Field2[A]): Lens[S, ev.A] = second(ev)

    def at[I, B](i: I)(implicit ev: At.Aux[A, I, B]): Lens[S, Option[B]] =
      optic.compose(ev.at(i))

    def cons(implicit ev: Cons[A]): Optional[S, (ev.A, A)] =
      optic.compose(ev.cons)

    def first(implicit ev: Field1[A]): Lens[S, ev.A] =
      optic.compose(ev.first)

    def headOption(implicit ev: Cons[A]): Optional[S, ev.A] =
      optic.compose(ev.headOption)

    def index[I, B](i: I)(implicit ev: Index.Aux[A, I, B]): Optional[S, B] =
      optic.compose(ev.index(i))

    def second(implicit ev: Field2[A]): Lens[S, ev.A] =
      optic.compose(ev.second)

    def tailOption(implicit ev: Cons[A]): Optional[S, A] =
      optic.compose(ev.tailOption)
  }
  
}

