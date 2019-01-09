package monocle.generic.internal

import scala.{Either => \/}
import shapeless._

/**
  * Typeclass converting a [[Coproduct]] to an [[\/]].
  *
  * Based on [[shapeless.ops.coproduct.CoproductToEither]]:
  * https://github.com/milessabin/shapeless/blob/shapeless-2.3.3-scala-2.13.0-M4/core/src/main/scala/shapeless/ops/coproduct.scala#L1275-L1303
  */
sealed trait CoproductToDisjunction[C <: Coproduct] extends DepFn1[C] with Serializable

object CoproductToDisjunction {
  type Aux[In <: Coproduct, Out0] = CoproductToDisjunction[In] { type Out = Out0 }

  implicit def baseToEither[L, R]: CoproductToDisjunction.Aux[L :+: R :+: CNil, L \/ R] = new CoproductToDisjunction[L :+: R :+: CNil] {
    type Out = L \/ R
    def apply(t: L :+: R :+: CNil): L \/ R = t match {
      case Inl(l)         => Left(l)
      case Inr(Inl(r))    => Right(r)
      case Inr(Inr(cnil)) => cnil.impossible
    }
  }

  implicit def cconsToEither[L, R <: Coproduct, Out0](implicit
                                                      evR: CoproductToDisjunction.Aux[R, Out0]
                                                     ): CoproductToDisjunction.Aux[L :+: R, L \/ Out0] = new CoproductToDisjunction[L :+: R] {
    type Out = L \/ Out0
    def apply(t: L :+: R): L \/ Out0 = t match {
      case Inl(l) => Left(l)
      case Inr(r) => Right(evR(r))
    }
  }
}
