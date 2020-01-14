package monocle.syntax

import monocle.function._
import monocle.{Optional, Prism}

object prism extends PrismSyntax

trait PrismSyntax {
  implicit class PrismSyntaxOps[From, To](optic: Prism[From, To]) {
    def at[Index, X](i: Index)(implicit ev: At.Aux[To, Index, X]): Optional[From, Option[X]] =
      optic.andThen(ev.at(i))

    def some[X](implicit ev: To =:= Option[X]): Prism[From, X] =
      optic.asTarget[Option[X]].andThen(Prism.some[X])
  }
}
