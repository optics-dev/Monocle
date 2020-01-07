package monocle.syntax

import monocle.{Prism, Setter}
import monocle.function._

object setter extends SetterSyntax

trait SetterSyntax {
  implicit class SetterOps[A, B](optic: Setter[A, B]) {
    def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): Setter[A, Option[C]] =
      optic.compose(ev.at(i))

    def some[C](implicit ev: B =:= Option[C]): Setter[A, C] =
      optic.asTarget[Option[C]].compose(Prism.some[C])
  }
}
