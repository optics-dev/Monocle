package monocle.std

import monocle.{Iso, PIso, PPrism, Prism}
import cats.data.{Chain, NonEmptyChain, OneAnd}

object nec extends NonEmptyChainOptics

trait NonEmptyChainOptics {
  final def pNecToOneAnd[A, B]: PIso[NonEmptyChain[A], NonEmptyChain[B], OneAnd[Chain, A], OneAnd[Chain, B]] =
    PIso((nec: NonEmptyChain[A]) => OneAnd[Chain, A](nec.head, nec.tail))(
      (oneAnd: OneAnd[Chain, B]) => NonEmptyChain.fromChainPrepend(oneAnd.head, oneAnd.tail)
    )

  final def necToOneAnd[A]: Iso[NonEmptyChain[A], OneAnd[Chain, A]] =
    pNecToOneAnd[A, A]

  final def pOptNecToChain[A, B]: PIso[Option[NonEmptyChain[A]], Option[NonEmptyChain[B]], Chain[A], Chain[B]] =
    PIso[Option[NonEmptyChain[A]], Option[NonEmptyChain[B]], Chain[A], Chain[B]](_.fold(Chain.empty[A])(_.toChain))(
      NonEmptyChain.fromChain
    )

  final def optNecToChain[A]: Iso[Option[NonEmptyChain[A]], Chain[A]] =
    pOptNecToChain[A, A]

  final def pChainToNec[A, B]: PPrism[Chain[A], Chain[B], NonEmptyChain[A], NonEmptyChain[B]] =
    PPrism((v: Chain[A]) => NonEmptyChain.fromChain[A](v).toRight(Chain.empty[B]))(
      (nec: NonEmptyChain[B]) => nec.toChain
    )

  final def chainToNec[A]: Prism[Chain[A], NonEmptyChain[A]] =
    pChainToNec[A, A]
}
