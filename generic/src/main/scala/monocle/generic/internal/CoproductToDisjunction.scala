package monocle.generic.internal

import shapeless._

/**
  * Typeclass converting a [[shapeless.Coproduct]] to an [[scala.Either]].
  *
  * Based on [[shapeless.ops.coproduct.CoproductToEither]]:
  * https://github.com/milessabin/shapeless/blob/shapeless-2.3.3-scala-2.13.0-M4/core/src/main/scala/shapeless/ops/coproduct.scala#L1275-L1303
  */
sealed trait CoproductToDisjunction[C <: Coproduct] extends DepFn1[C] with Serializable

object CoproductToDisjunction {
  type Aux[In <: Coproduct, Out0] = CoproductToDisjunction[In] { type Out = Out0 }

  implicit def baseToEither[L, R]: CoproductToDisjunction.Aux[L :+: R :+: CNil, Either[L, R]] = new CoproductToDisjunction[L :+: R :+: CNil] {
    type Out = Either[L, R]
    def apply(t: L :+: R :+: CNil): Either[L, R] = t match {
      case Inl(l)         => Left(l)
      case Inr(Inl(r))    => Right(r)
      case Inr(Inr(cnil)) => cnil.impossible
    }
  }

  implicit def cconsToEither[L, R <: Coproduct, Out0](implicit
    evR: CoproductToDisjunction.Aux[R, Out0]
  ): CoproductToDisjunction.Aux[L :+: R, Either[L, Out0]] = new CoproductToDisjunction[L :+: R] {
    type Out = Either[L, Out0]
    def apply(t: L :+: R): Either[L, Out0] = t match {
      case Inl(l) => Left(l)
      case Inr(r) => Right(evR(r))
    }
  }
}
