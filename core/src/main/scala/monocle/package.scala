
package object monocle {

  type SimpleLens[S, A] = Lens[S, S, A, A]
  type SimpleTraversal[S, A] = Traversal[S, S, A, A]
  type SimpleSetter[S, A] = Setter[S, S, A, A]

  object SimpleLens {
    def apply[S, A](_get: S => A, _set: (S, A) => S): SimpleLens[S, A] = Lens[S, S, A, A](_get, _set)
  }

}
