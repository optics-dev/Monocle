package monocle.syntax

import monocle.Lens

object iso extends IsoSyntax

trait IsoSyntax {
  implicit class IsoAppliedOps[A](value: A){
    def optic: AppliedIso[A, A] =
      AppliedIso.id(value)

    def optic[B](lens: Lens[A, B]): AppliedLens[A, B] =
      AppliedLens(value, lens)
  }
}