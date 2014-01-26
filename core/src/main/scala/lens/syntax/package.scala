package lens


package object syntax {

  implicit class RichLens[A](from: A) {
    def >-[B](lens: Lens[A, B]): AppliedLens[A, B] = new AppliedLens(from, lens)
  }

  implicit class RichTraversal[A](from: A) {
    def ->-[B](traversal: Traversal[A, B]): AppliedTraversal[A, B] = new AppliedTraversal(from, traversal)
  }

  implicit class RichSetter[A](from: A) {
    def >--[B](setter: Setter[A, B]): AppliedSetter[A, B] = new AppliedSetter(from, setter)
  }

}
