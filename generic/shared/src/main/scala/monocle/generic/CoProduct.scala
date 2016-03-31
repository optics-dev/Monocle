package monocle.generic

import monocle.Prism
import shapeless.Coproduct
import shapeless.ops.coproduct.{Inject, Selector}

object coproduct extends CoProductInstances


trait CoProductInstances {
  
  def coProductPrism[C <: Coproduct, A](implicit evInject: Inject[C, A], evSelector: Selector[C, A]): Prism[C, A] =
    Prism[C, A](evSelector.apply(_))(evInject.apply)
  

}