package monocle.syntax

import monocle.{Optional, Prism}
import monocle.function._

object optional extends LensSyntax

trait OptionalSyntax {
  implicit class OptionalOps[A, B](optic: Optional[A, B]) {
    def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): Optional[A, Option[C]] =
      optic.compose(ev.at(i))

    def some[C](implicit ev: B =:= Option[C]): Optional[A, C] =
      optic.asTarget[Option[C]].compose(Prism.some[C])
  }
}
