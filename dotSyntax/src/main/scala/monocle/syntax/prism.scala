package monocle.syntax

import monocle.function._
import monocle.{Optional, Prism}

object prism extends PrismSyntax

trait PrismSyntax {
  implicit class PrismSyntaxOps[A, B](optic: Prism[A, B]) {
    def _1(implicit ev: Field1[B]): Optional[A, ev.A] = first(ev)
    def _2(implicit ev: Field2[B]): Optional[A, ev.A] = second(ev)

    def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): Optional[A, Option[C]] =
      optic.compose(ev.at(i))

    def cons(implicit ev: Cons[B]): Prism[A, (ev.A, B)] =
      optic.compose(ev.cons)

    def first(implicit ev: Field1[B]): Optional[A, ev.A] =
      optic.compose(ev.first)

    def headOption(implicit ev: Cons[B]): Optional[A, ev.A] =
      optic.compose(ev.headOption)

    def index[I, C](i: I)(implicit ev: Index.Aux[B, I, C]): Optional[A, C] =
      optic.compose(ev.index(i))

    def second(implicit ev: Field2[B]): Optional[A, ev.A] =
      optic.compose(ev.second)

    def tailOption(implicit ev: Cons[B]): Optional[A, B] =
      optic.compose(ev.tailOption)
  }

}

