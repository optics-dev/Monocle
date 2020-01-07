package monocle.syntax

import monocle.function._
import monocle.{Iso, Lens, Prism}

object iso extends IsoSyntax

trait IsoSyntax {
  implicit class IsoOps[From, To](optic: Iso[From, To]) {
    def at[Index, X](i: Index)(implicit ev: At.Aux[To, Index, X]): Lens[From, Option[X]] =
      optic.compose(ev.at(i))

    def some[X](implicit ev: To =:= Option[X]): Prism[From, X] =
      optic.asTarget[Option[X]].compose(Prism.some[X])
  }
}
