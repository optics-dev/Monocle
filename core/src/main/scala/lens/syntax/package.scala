package lens


package object syntax {

  implicit class RichLens[A](from: A) {
    def >-[B](lens: Lens[A, B]): AppliedLens[A, B] = new AppliedLens(from, lens)
  }

  implicit class RichSetter[A](from: A) {
    def >--[B](setter: Setter[A, B]): AppliedSetter[A, B] = new AppliedSetter(from, setter)
  }

}
