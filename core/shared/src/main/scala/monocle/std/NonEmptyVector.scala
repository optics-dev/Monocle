package monocle.std

import monocle.{Iso, PIso}
import cats.data.{NonEmptyVector, OneAnd}

import scala.{Vector => IVector}

object nev extends NonEmptyVectorOptics

trait NonEmptyVectorOptics {

  final def pNevToOneAnd[A, B]: PIso[NonEmptyVector[A], NonEmptyVector[B], OneAnd[Vector,A], OneAnd[Vector,B]] =
    PIso((nev: NonEmptyVector[A])    => OneAnd[Vector,A](nev.head, nev.tail))(
      (oneAnd: OneAnd[Vector, B]) => NonEmptyVector(oneAnd.head, oneAnd.tail))

  final def nevToOneAnd[A]: Iso[NonEmptyVector[A], OneAnd[Vector,A]] =
    pNevToOneAnd[A, A]

  final def pOptNevToVector[A, B]: PIso[Option[NonEmptyVector[A]], Option[NonEmptyVector[B]], Vector[A], Vector[B]] =
    PIso[Option[NonEmptyVector[A]], Option[NonEmptyVector[B]], IVector[A], IVector[B]](_.fold(IVector.empty[A])(_.toVector))(
      NonEmptyVector.fromVector
    )

  final def optNevToVector[A]: Iso[Option[NonEmptyVector[A]], Vector[A]] =
    pOptNevToVector[A, A]

}
