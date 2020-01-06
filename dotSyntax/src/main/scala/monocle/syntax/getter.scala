package monocle.syntax

import monocle.Getter
import monocle.function._

object getter extends GetterSyntax

trait GetterSyntax {
  implicit class GetterOps[A, B](optic: Getter[A, B]) {
    def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): Getter[A, Option[C]] =
      optic.compose(ev.at(i))
  }
}
