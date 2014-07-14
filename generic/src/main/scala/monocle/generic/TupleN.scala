package monocle.generic

import monocle.SimpleIso
import monocle.function.Reverse
import shapeless.ops.tuple.{Reverse => TReverse}


object tuplen extends TupleNInstances

trait TupleNInstances {

  implicit def tupleReverse[S, A](implicit evRev1: TReverse.Aux[S, A],
                                           evRev2: TReverse.Aux[A, S]): Reverse[S, A] = new Reverse[S, A]{
    def reverse = SimpleIso[S, A](s => evRev1.apply(s),a => evRev2.apply(a))
  }

}
