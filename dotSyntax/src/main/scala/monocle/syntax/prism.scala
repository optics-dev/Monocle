package monocle.syntax

import monocle.function._
import monocle.{Optional, Prism}

object prism extends PrismSyntax

trait PrismSyntax {
  implicit class PrismSyntaxOps[A, B](optic: Prism[A, B]) {
    def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): Optional[A, Option[C]] =
      optic.compose(ev.at(i))

    def some[C](implicit ev: B =:= Option[C]): Prism[A, C] =
      optic.asTarget[Option[C]].compose(Prism.some[C])
  }
}
