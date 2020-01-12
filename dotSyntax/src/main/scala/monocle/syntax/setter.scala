package monocle.syntax

import monocle.{Prism, Setter}
import monocle.function._

object setter extends SetterSyntax

trait SetterSyntax {
  implicit class SetterOps[From, To](optic: Setter[From, To]) {
    def at[Index, X](i: Index)(implicit ev: At.Aux[To, Index, X]): Setter[From, Option[X]] =
      optic.andThen(ev.at(i))

    def some[X](implicit ev: To =:= Option[X]): Setter[From, X] =
      optic.asTarget[Option[X]].andThen(Prism.some[X])
  }
}
