package monocle.generic

import monocle.Iso
import shapeless.Generic

object generic extends GenericOptics

trait GenericOptics {
  /** An isomorphism between a type `S` and its generic representation. */
  def toGeneric[S](implicit S: Generic[S]): Iso[S, S.Repr] =
    Iso[S, S.Repr](S.to(_))(S.from(_))
}
