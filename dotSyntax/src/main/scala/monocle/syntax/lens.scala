package monocle.syntax

import monocle.{Lens, Optional, Prism}
import monocle.function._

object lens extends LensSyntax

trait LensSyntax {
  implicit class LensOps[From, To](optic: Lens[From, To]) {
    def at[Index, X](i: Index)(implicit ev: At.Aux[To, Index, X]): Lens[From, Option[X]] =
      optic.compose(ev.at(i))

    def some[X](implicit ev: To =:= Option[X]): Optional[From, X] =
      optic.asTarget[Option[X]].compose(Prism.some[X])
  }
}
