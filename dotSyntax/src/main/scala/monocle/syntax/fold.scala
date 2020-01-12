package monocle.syntax

import monocle.function._
import monocle.{Fold, Prism}

object fold extends FoldSyntax

trait FoldSyntax {
  implicit class FoldOps[From, To](optic: Fold[From, To]) {
    def at[Index, X](i: Index)(implicit ev: At.Aux[To, Index, X]): Fold[From, Option[X]] =
      optic.andThen(ev.at(i))

    def some[X](implicit ev: To =:= Option[X]): Fold[From, X] =
      optic.asTarget[Option[X]].andThen(Prism.some[X])
  }
}
