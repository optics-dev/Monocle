package monocle.generic.internal

import scalaz.{\/, -\/, \/-}
import shapeless._

/**
  * Typeclass converting an [[Either]] to a [[\/]].
  *
  * Based on [[shapeless.ops.coproduct.EitherToCoproduct]]:
  * https://github.com/milessabin/shapeless/blob/shapeless-2.3.3-scala-2.13.0-M4/core/src/main/scala/shapeless/ops/coproduct.scala#L1305-L1335
  */
sealed trait DisjunctionToCoproduct[L, R] extends DepFn1[L \/ R] with Serializable { type Out <: Coproduct }

object DisjunctionToCoproduct extends DisjunctionToCoproductLowPrio {
  type Aux[L, R, Out0 <: Coproduct] = DisjunctionToCoproduct[L, R] { type Out = Out0 }

  implicit def econsDisjunctionToCoproduct[L, RL, RR, Out0 <: Coproduct](implicit
    evR: DisjunctionToCoproduct.Aux[RL, RR, Out0]
  ): DisjunctionToCoproduct.Aux[L, RL \/ RR, L :+: Out0] = new DisjunctionToCoproduct[L, RL \/ RR] {
    type Out = L :+: Out0
    def apply(t: L \/ (RL \/ RR)): L :+: Out0 = t match {
      case -\/(l) => Inl(l)
      case \/-(r) => Inr(evR(r))
    }
  }
}

trait DisjunctionToCoproductLowPrio {
  implicit def baseDisjunctionToCoproduct[L, R]: DisjunctionToCoproduct.Aux[L, R, L :+: R :+: CNil] = new DisjunctionToCoproduct[L, R] {
    type Out = L :+: R :+: CNil

    def apply(t: L \/ R): L :+: R :+: CNil = t match {
      case -\/(l) => Inl(l)
      case \/-(r) => Coproduct[L :+: R :+: CNil](r)
    }
  }
}
