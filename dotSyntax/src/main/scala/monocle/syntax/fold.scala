package monocle.syntax

import monocle.function._
import monocle.{Fold, Prism}

object fold extends FoldSyntax

trait FoldSyntax {
  implicit class FoldOps[A, B](optic: Fold[A, B]) {
    def _1(implicit ev: Field1[B]): Fold[A, ev.B] = first(ev)
    def _2(implicit ev: Field2[B]): Fold[A, ev.B] = second(ev)
    def _3(implicit ev: Field3[B]): Fold[A, ev.B] = third(ev)
    def _4(implicit ev: Field4[B]): Fold[A, ev.B] = fourth(ev)
    def _5(implicit ev: Field5[B]): Fold[A, ev.B] = fifth(ev)
    def _6(implicit ev: Field6[B]): Fold[A, ev.B] = sixth(ev)

    def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): Fold[A, Option[C]] =
      optic.compose(ev.at(i))

    def cons(implicit ev: Cons[B]): Fold[A, (ev.B, B)] =
      optic.compose(ev.cons)

    def first(implicit ev: Field1[B]): Fold[A, ev.B] =
      optic.compose(ev.first)

    def headOption(implicit ev: Cons[B]): Fold[A, ev.B] =
      optic.compose(ev.headOption)

    def index[I, C](i: I)(implicit ev: Index.Aux[B, I, C]): Fold[A, C] =
      optic.compose(ev.index(i))

    def left[E, C](implicit ev: B =:= Either[E, C]): Fold[A, E] =
      optic.asTarget[Either[E, C]].compose(Prism.left[E, C])

    def right[E, C](implicit ev: B =:= Either[E, C]): Fold[A, C] =
      optic.asTarget[Either[E, C]].compose(Prism.right[E, C])

    def second(implicit ev: Field2[B]): Fold[A, ev.B] =
      optic.compose(ev.second)

    def third(implicit ev: Field3[B]): Fold[A, ev.B] =
      optic.compose(ev.third)

    def fourth(implicit ev: Field4[B]): Fold[A, ev.B] =
      optic.compose(ev.fourth)

    def fifth(implicit ev: Field5[B]): Fold[A, ev.B] =
      optic.compose(ev.fifth)

    def sixth(implicit ev: Field6[B]): Fold[A, ev.B] =
      optic.compose(ev.sixth)

    def some[C](implicit ev: B =:= Option[C]): Fold[A, C] =
      optic.asTarget[Option[C]].compose(Prism.some[C])

    def tailOption(implicit ev: Cons[B]): Fold[A, B] =
      optic.compose(ev.tailOption)

    def reverse(implicit ev: Reverse[B]): Fold[A, ev.B] =
      optic.compose(ev.reverse)
  }
}
