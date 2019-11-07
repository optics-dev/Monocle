package object monocle {
  type Iso[S, A]      = PIso[S, S, A, A]
  type Lens[S, A]     = PLens[S, S, A, A]
  type Optional[S, A] = POptional[S, S, A, A]
  type Prism[S, A]    = PPrism[S, S, A, A]
}
