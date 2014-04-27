package monocle.thirdparty

import monocle.SimplePrism
import shapeless.Coproduct
import shapeless.ops.coproduct.{Selector, Inject}
import monocle.function.SafeCast


object coproduct extends CoProductInstances


trait CoProductInstances {
  
  implicit def coProductSafeCase[C <: Coproduct, A](implicit evInject: Inject[C, A], evSelector: Selector[C, A]): SafeCast[C, A] =
    new SafeCast[C, A] {
      def safeCast: SimplePrism[C, A] = SimplePrism[C, A](evInject.apply, evSelector.apply)
    }
  

}