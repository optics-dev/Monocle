package monocle.syntax

import monocle.{Lens, Optional, Prism}

object applied extends AppliedSyntax

trait AppliedSyntax {
  implicit class AppliedOps[A](value: A) {
    def optic: AppliedIso[A, A] =
      AppliedIso.id(value)

    def optic[B](lens: Lens[A, B]): AppliedLens[A, B] =
      AppliedLens(value, lens)

    def optic[B](prism: Prism[A, B]): AppliedPrism[A, B] =
      AppliedPrism(value, prism)

    def optic[B](optional: Optional[A, B]): AppliedOptional[A, B] =
      AppliedOptional(value, optional)
  }
}
