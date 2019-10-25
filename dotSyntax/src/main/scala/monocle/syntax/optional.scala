package monocle.syntax

import monocle.{Optional, Prism}
import monocle.function._

object optional extends LensSyntax

trait OptionalSyntax {
  implicit class OptionalOps[A, B](optic: Optional[A, B]) {
    def _1(implicit ev: Field1[B]): Optional[A, ev.A] = first(ev)
    def _2(implicit ev: Field2[B]): Optional[A, ev.A] = second(ev)

    def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): Optional[A, Option[C]] =
      optic.compose(ev.at(i))

    def cons(implicit ev: Cons[B]): Optional[A, (ev.A, B)] =
      optic.compose(ev.cons)

    def first(implicit ev: Field1[B]): Optional[A, ev.A] =
      optic.compose(ev.first)

    def headOption(implicit ev: Cons[B]): Optional[A, ev.A] =
      optic.compose(ev.headOption)

    def index[I, C](i: I)(implicit ev: Index.Aux[B, I, C]): Optional[A, C] =
      optic.compose(ev.index(i))

    def left[E, C](implicit ev: B =:= Either[E, C]): Optional[A, E] =
      optic.asTarget[Either[E, C]].compose(Prism.left[E, C])

    def right[E, C](implicit ev: B =:= Either[E, C]): Optional[A, C] =
      optic.asTarget[Either[E, C]].compose(Prism.right[E, C])

    def second(implicit ev: Field2[B]): Optional[A, ev.A] =
      optic.compose(ev.second)

    def third(implicit ev: Field3[B]): Optional[A, ev.A] =
      optic.compose(ev.third)

    def some[C](implicit ev: B =:= Option[C]): Optional[A, C] =
      optic.asTarget[Option[C]].compose(Prism.some[C])

    def tailOption(implicit ev: Cons[B]): Optional[A, B] =
      optic.compose(ev.tailOption)
  }

}
