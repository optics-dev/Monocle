package monocle.syntax

import monocle.Getter
import monocle.function._

object getter extends GetterSyntax

trait GetterSyntax {
  implicit class GetterOps[A, B](optic: Getter[A, B]) {
    def _1(implicit ev: Field1[B]): Getter[A, ev.B] = first(ev)
    def _2(implicit ev: Field2[B]): Getter[A, ev.B] = second(ev)
    def _3(implicit ev: Field3[B]): Getter[A, ev.B] = third(ev)
    def _4(implicit ev: Field4[B]): Getter[A, ev.B] = fourth(ev)
    def _5(implicit ev: Field5[B]): Getter[A, ev.B] = fifth(ev)
    def _6(implicit ev: Field6[B]): Getter[A, ev.B] = sixth(ev)

    def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): Getter[A, Option[C]] =
      optic.compose(ev.at(i))

    def first(implicit ev: Field1[B]): Getter[A, ev.B] =
      optic.compose(ev.first)

    def second(implicit ev: Field2[B]): Getter[A, ev.B] =
      optic.compose(ev.second)

    def third(implicit ev: Field3[B]): Getter[A, ev.B] =
      optic.compose(ev.third)

    def fourth(implicit ev: Field4[B]): Getter[A, ev.B] =
      optic.compose(ev.fourth)

    def fifth(implicit ev: Field5[B]): Getter[A, ev.B] =
      optic.compose(ev.fifth)

    def sixth(implicit ev: Field6[B]): Getter[A, ev.B] =
      optic.compose(ev.sixth)

    def reverse(implicit ev: Reverse[B]): Getter[A, ev.B] =
      optic.compose(ev.reverse)
  }
}
