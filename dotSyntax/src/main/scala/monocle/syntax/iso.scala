package monocle.syntax

import monocle.function._
import monocle.{Iso, Lens, Prism}

object iso extends IsoSyntax

trait IsoSyntax {
  implicit class IsoOps[A, B](optic: Iso[A, B]) {
    def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): Lens[A, Option[C]] =
      optic.compose(ev.at(i))

    def some[C](implicit ev: B =:= Option[C]): Prism[A, C] =
      optic.asTarget[Option[C]].compose(Prism.some[C])
  }
}
