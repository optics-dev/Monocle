package lens


package object syntax {

  implicit class RichLens[A](from: A) {
    def >-[B](lens: Lens[A, B]): AppliedLens[A, B] = AppliedLens(from, lens)
  }

}
