import monocle.syntax._

package object monocle {
  type Setter[S, A]    = PSetter[S, S, A, A]
  type Traversal[S, A] = PTraversal[S, S, A, A]
  type Optional[S, A]  = POptional[S, S, A, A]
  type Prism[S, A]     = PPrism[S, S, A, A]
  type Lens[S, A]      = PLens[S, S, A, A]
  type Iso[S, A]       = PIso[S, S, A, A]

  type AppliedSetter[S, A]    = AppliedPSetter[S, S, A, A]
  type AppliedTraversal[S, A] = AppliedPTraversal[S, S, A, A]
  type AppliedOptional[S, A]  = AppliedPOptional[S, S, A, A]
  type AppliedPrism[S, A]     = AppliedPPrism[S, S, A, A]
  type AppliedLens[S, A]      = AppliedPLens[S, S, A, A]
  type AppliedIso[S, A]       = AppliedPIso[S, S, A, A]
}
