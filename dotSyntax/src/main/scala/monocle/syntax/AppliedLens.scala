package monocle.syntax

import monocle.function.{At, Field1, Field2}
import monocle.{Iso, Lens}
import monocle.syntax.lens._


class AppliedLens[S, A](value: S, optic: Lens[S, A]) {
  def _1(implicit ev: Field1[A]): AppliedLens[S, ev.A] = first(ev)
  def _2(implicit ev: Field2[A]): AppliedLens[S, ev.A] = second(ev)
  def at[I, B](i: I)(implicit ev: At.Aux[A, I, B]): AppliedLens[S, Option[B]] = new AppliedLens(value, optic.at(i))
  def first(implicit ev: Field1[A]): AppliedLens[S, ev.A] = new AppliedLens(value, optic.first)
  def second(implicit ev: Field2[A]): AppliedLens[S, ev.A] = new AppliedLens(value, optic.second)

}

object AppliedLens {
  def id[A](value: A): AppliedLens[A, A] =
    new AppliedLens(value, Iso.id)
}
