import scalaz.Traverse

package object monocle {

  type SLens[S, A] = Lens[S, S, A, A]

  object SLens {
    def apply[S, A](_get: S => A, _set: (S, A) => S): SLens[S, A] = Lens(_get, _set)
  }

  type STraversal[S, A] = Traversal[S, S, A, A]

  object STraversal {
    def apply[T[_]: Traverse, A]: STraversal[T[A], A] = Traversal[T, A, A]
  }


}
