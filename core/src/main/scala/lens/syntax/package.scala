package lens

import scalaz.Traverse

package object syntax {

  implicit class RichLens[A](from: A) {
    def >-[B](lens: Lens[A, B]): AppliedLens[A, B] = AppliedLens(from, lens)
  }

  implicit class RichTraversal[A](from: A) {
    def >--[B](traversal: Traversal[A, B]): AppliedTraversal[A, B] = AppliedTraversal(from, traversal)
  }

  implicit class RichTraversal2[T[_] : Traverse, A](from: T[A]) {
    def traverse: AppliedTraversal[T[A], A] = AppliedTraversal(from, Traversal[T, A])
  }

}
