package monocle.syntax

import monocle.{Lens, Optional, Prism}
import monocle.function._

object lens extends LensSyntax

trait LensSyntax {
  implicit class LensOps[A, B](optic: Lens[A, B]) {
    def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): Lens[A, Option[C]] =
      optic.compose(ev.at(i))

    def some[C](implicit ev: B =:= Option[C]): Optional[A, C] =
      optic.asTarget[Option[C]].compose(Prism.some[C])
  }
}
