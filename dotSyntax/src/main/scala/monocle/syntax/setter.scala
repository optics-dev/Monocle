package monocle.syntax

import monocle.Setter
import monocle.function._

object setter extends SetterSyntax

trait SetterSyntax {
  implicit class SetterOps[A, B](optic: Setter[A, B]) {
    def _1(implicit ev: Field1[B]): Setter[A, ev.B] = first(ev)
    def _2(implicit ev: Field2[B]): Setter[A, ev.B] = second(ev)
    def _3(implicit ev: Field3[B]): Setter[A, ev.B] = third(ev)
    def _4(implicit ev: Field4[B]): Setter[A, ev.B] = fourth(ev)
    def _5(implicit ev: Field5[B]): Setter[A, ev.B] = fifth(ev)
    def _6(implicit ev: Field6[B]): Setter[A, ev.B] = sixth(ev)

    def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): Setter[A, Option[C]] =
      optic.compose(ev.at(i))

    def first(implicit ev: Field1[B]): Setter[A, ev.B] =
      optic.compose(ev.first)

    def second(implicit ev: Field2[B]): Setter[A, ev.B] =
      optic.compose(ev.second)

    def third(implicit ev: Field3[B]): Setter[A, ev.B] =
      optic.compose(ev.third)

    def fourth(implicit ev: Field4[B]): Setter[A, ev.B] =
      optic.compose(ev.fourth)

    def fifth(implicit ev: Field5[B]): Setter[A, ev.B] =
      optic.compose(ev.fifth)

    def sixth(implicit ev: Field6[B]): Setter[A, ev.B] =
      optic.compose(ev.sixth)
  }
}
