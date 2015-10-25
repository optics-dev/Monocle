package monocle.generic

import monocle.Iso
import monocle.generic.internal.TupleGeneric

object product extends ProductOptics

trait ProductOptics {
  def productToTuple[S <: Product](implicit ev: TupleGeneric[S]): Iso[S, ev.Repr] =
    Iso[S, ev.Repr](s => ev.to(s))(t => ev.from(t))
}