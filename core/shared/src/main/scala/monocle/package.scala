import monocle.syntax._

package object monocle {
  type Setter[S, A]    = PSetter[S, S, A, A]
  type Traversal[S, A] = PTraversal[S, S, A, A]
  type Optional[S, A]  = POptional[S, S, A, A]
  type Prism[S, A]     = PPrism[S, S, A, A]
  type Lens[S, A]      = PLens[S, S, A, A]
  type Iso[S, A]       = PIso[S, S, A, A]

  type ApplySetter[S, A]    = ApplyPSetter[S, S, A, A]
  type ApplyTraversal[S, A] = ApplyPTraversal[S, S, A, A]
  type ApplyOptional[S, A]  = ApplyPOptional[S, S, A, A]
  type ApplyPrism[S, A]     = ApplyPPrism[S, S, A, A]
  type ApplyLens[S, A]      = ApplyPLens[S, S, A, A]
  type ApplyIso[S, A]       = ApplyPIso[S, S, A, A]
}
