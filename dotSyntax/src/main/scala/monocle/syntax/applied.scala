package monocle.syntax

import monocle.{Fold, Getter, Lens, Optional, Prism, Setter}

object applied extends AppliedSyntax

trait AppliedSyntax {
  implicit class AppliedOps[From](value: From) {
    def optic: AppliedIso[From, From] =
      AppliedIso.id(value)

    def optic[To](lens: Lens[From, To]): AppliedLens[From, To] =
      AppliedLens(value, lens)

    def optic[To](prism: Prism[From, To]): AppliedPrism[From, To] =
      AppliedPrism(value, prism)

    def optic[To](optional: Optional[From, To]): AppliedOptional[From, To] =
      AppliedOptional(value, optional)

    def optic[To](getter: Getter[From, To]): AppliedGetter[From, To] =
      AppliedGetter(value, getter)

    def optic[To](fold: Fold[From, To]): AppliedFold[From, To] =
      AppliedFold(value, fold)

    def optic[To](setter: Setter[From, To]): AppliedSetter[From, To] =
      AppliedSetter(value, setter)
  }
}
