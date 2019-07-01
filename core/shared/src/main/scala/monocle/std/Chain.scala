package monocle.std

import cats.data.Chain
import monocle.{Iso, PIso}

object chain extends ChainOptics

trait ChainOptics {
  def pChainToList[A, B]: PIso[Chain[A], Chain[B], List[A], List[B]] =
    PIso[Chain[A], Chain[B], List[A], List[B]](_.toList)(Chain.fromSeq)

  def chainToList[A]: Iso[Chain[A], List[A]] =
    pChainToList[A, A]

  def pChainToVector[A, B]: PIso[Chain[A], Chain[B], Vector[A], Vector[B]] =
    PIso[Chain[A], Chain[B], Vector[A], Vector[B]](_.toVector)(Chain.fromSeq)

  def chainToVector[A]: Iso[Chain[A], Vector[A]] =
    pChainToVector[A, A]
}
