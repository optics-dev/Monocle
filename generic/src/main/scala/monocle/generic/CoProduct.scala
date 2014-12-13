package monocle.generic

import monocle.Prism
import shapeless.Coproduct
import shapeless.ops.coproduct.{Inject, Selector}

import scalaz.syntax.std.option._


object coproduct extends CoProductInstances


trait CoProductInstances {
  
  def coProductPrism[C <: Coproduct, A](implicit evInject: Inject[C, A], evSelector: Selector[C, A]): Prism[C, A] =
    Prism[C, A](evSelector.apply(_).toMaybe)(evInject.apply)
  

}