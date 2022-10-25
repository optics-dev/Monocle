package monocle.generic

import monocle.Iso
import monocle.function.Reverse
import shapeless.ops.tuple.{Reverse => TReverse}

@deprecated("no replacement", since = "3.0.0-M1")
object tuplen extends TupleNInstances

trait TupleNInstances {
  @deprecated("no replacement", since = "3.0.0-M1")
  implicit def tupleReverse[S, A](implicit evRev1: TReverse.Aux[S, A], evRev2: TReverse.Aux[A, S]): Reverse[S, A] =
    new Reverse[S, A] {
      def reverse = Iso[S, A](evRev1.apply)(evRev2.apply)
    }
}
