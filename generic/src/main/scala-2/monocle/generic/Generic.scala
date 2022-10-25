package monocle.generic

import monocle.Iso
import shapeless.Generic

@deprecated("no replacement", since = "3.0.0-M1")
object generic extends GenericOptics

trait GenericOptics {

  /** An isomorphism between a type `S` and its generic representation. */
  @deprecated("no replacement", since = "3.0.0-M1")
  def toGeneric[S](implicit S: Generic[S]): Iso[S, S.Repr] =
    Iso[S, S.Repr](S.to(_))(S.from(_))
}
