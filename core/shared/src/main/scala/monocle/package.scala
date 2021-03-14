package object monocle {
  type Setter[S, A]    = PSetter[S, S, A, A]
  type Traversal[S, A] = PTraversal[S, S, A, A]
  type Optional[S, A]  = POptional[S, S, A, A]
  type Prism[S, A]     = PPrism[S, S, A, A]
  type Lens[S, A]      = PLens[S, S, A, A]
  type Iso[S, A]       = PIso[S, S, A, A]

  type AppliedSetter[S, A]    = syntax.AppliedPSetter[S, S, A, A]
  type AppliedTraversal[S, A] = syntax.AppliedPTraversal[S, S, A, A]
  type AppliedOptional[S, A]  = syntax.AppliedPOptional[S, S, A, A]
  type AppliedPrism[S, A]     = syntax.AppliedPPrism[S, S, A, A]
  type AppliedLens[S, A]      = syntax.AppliedPLens[S, S, A, A]
  type AppliedIso[S, A]       = syntax.AppliedPIso[S, S, A, A]

  type AppliedGetter[S, A] = syntax.AppliedGetter[S, A]
  type AppliedFold[S, A]   = syntax.AppliedFold[S, A]
}
