package object monocle {
  type Iso[S, A]      = PIso[S, S, A, A]
  type Lens[S, A]     = PLens[S, S, A, A]
  type Optional[S, A] = POptional[S, S, A, A]
  type Prism[S, A]    = PPrism[S, S, A, A]
  type Setter[S, A]   = PSetter[S, S, A, A]
  type Getter[S, A]   = PGetter[S, S, A, A]
  type Fold[S, A]     = PFold[S, S, A, A]
}
