package monocle.syntax

import monocle.Getter
import monocle.function._

object getter extends GetterSyntax

trait GetterSyntax {
  implicit class GetterOps[From, To](optic: Getter[From, To]) {
    def at[Index, X](i: Index)(implicit ev: At.Aux[To, Index, X]): Getter[From, Option[X]] =
      optic.andThen(ev.at(i))
  }
}
