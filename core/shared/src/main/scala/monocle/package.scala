package object monocle {

  type Setter[S, A]    = PSetter[S, S, A, A]
  type Traversal[S, A] = PTraversal[S, S, A, A]
  type Optional[S, A]  = POptional[S, S, A, A]
  type Prism[S, A]     = PPrism[S, S, A, A]
  type Lens[S, A]      = PLens[S, S, A, A]
  type Iso[S, A]       = PIso[S, S, A, A]

  type ITraversal[I, S, A] = IPTraversal[I, S, S, A, A]
}
