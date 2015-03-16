package monocle.std

import monocle.{Iso, PIso, PPrism, Prism}

import scalaz.{-\/, \/, \/-}

object disjunction extends DisjunctionFunctions

trait DisjunctionFunctions {

  /** [[PIso]] between a [[scalaz.Disjunction]] and an [[scala.Either]] */
  def pDisjunctionToEither[A, B, C, D]: PIso[A \/ B, C \/ D, Either[A, B], Either[C, D]] =
    PIso((_: A \/ B).toEither)(\/.fromEither)

  /** monomorphic alias for pDisjunctionToEither */
  def disjunctionToEither[A, B]: Iso[A \/ B, Either[A, B]] =
    pDisjunctionToEither[A, B, A, B]

  /** [[PPrism]] toward the left side of a [[scalaz.Disjunction]] */
  def pLeft[A, B, C]: PPrism[A \/ B, C \/ B, A, C] =
    PPrism[A \/ B, C \/ B, A, C](_.swap.bimap(\/-.apply, identity))(-\/.apply)

  /** monomorphic alias for pLeft */
  def left[A, B]: Prism[A \/ B, A] =
    pLeft[A, B, A]

  /** [[PPrism]] toward the right side of a [[scalaz.Disjunction]] */
  def pRight[A, B, C]: PPrism[A \/ B, A \/ C, B, C] =
    PPrism[A \/ B, A \/ C, B, C](_.bimap(-\/.apply, identity))(\/-.apply)

  /** monomorphic alias for pright */
  def right[A, B]: Prism[A \/ B, B] =
    pRight[A, B, B]
}
