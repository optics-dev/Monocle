package monocle.syntax

import monocle.{Optional, Prism}
import monocle.function._

object optional extends LensSyntax

trait OptionalSyntax {
  implicit class OptionalOps[From, To](optic: Optional[From, To]) {
    def at[Index, X](i: Index)(implicit ev: At.Aux[To, Index, X]): Optional[From, Option[X]] =
      optic.andThen(ev.at(i))

    def some[X](implicit ev: To =:= Option[X]): Optional[From, X] =
      optic.asTarget[Option[X]].andThen(Prism.some[X])
  }
}
