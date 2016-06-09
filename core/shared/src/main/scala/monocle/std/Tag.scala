package monocle.std

import monocle.Iso
import monocle.function.Wrapped

import scalaz.{@@, Tag}

object tag extends TagOptics

trait TagOptics {
  implicit def tagWrapped[A, B]: Wrapped[A @@ B, A] =
    new Wrapped[A @@ B, A] {
      val wrapped: Iso[A @@ B, A] =
        Iso(Tag.unwrap[A, B])(Tag.apply)
    }
}
