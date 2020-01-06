package monocle.syntax

import monocle.function._
import monocle.{Fold, Prism}

object fold extends FoldSyntax

trait FoldSyntax {
  implicit class FoldOps[A, B](optic: Fold[A, B]) {
    def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): Fold[A, Option[C]] =
      optic.compose(ev.at(i))

    def some[C](implicit ev: B =:= Option[C]): Fold[A, C] =
      optic.asTarget[Option[C]].compose(Prism.some[C])
  }
}
