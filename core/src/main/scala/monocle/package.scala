package object monocle {

  type Optic[P[_, _], S, T, A, B] = P[A, B] => P[S, T]

  type Setter[S, A]    = PSetter[S, S, A, A]
  type Traversal[S, A] = PTraversal[S, S, A, A]
  type Optional[S, A]  = POptional[S, S, A, A]
  type Prism[S, A]     = PPrism[S, S, A, A]
  type Lens[S, A]      = PLens[S, S, A, A]
  type Iso[S, A]       = PIso[S, S, A, A]

}
