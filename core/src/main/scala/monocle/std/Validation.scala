package monocle.std

import monocle.{PIso, PPrism}

import scalaz.syntax.either._
import scalaz.syntax.validation._
import scalaz.{Validation, \/}

object validation extends ValidationFunctions

trait ValidationFunctions {
  final def success[E, A, B]: PPrism[Validation[E, A], Validation[E, B], A, B] =
    PPrism[Validation[E, A], Validation[E, B], A, B](
      _.fold(e => Validation.failure[E, B](e).left[A], a => a.right[Validation[E, B]])
    )(_.success[E])

  final def failure[E, A, B]: PPrism[Validation[E, A], Validation[B, A], E, B] =
    PPrism[Validation[E, A], Validation[B, A], E, B](
      _.fold(e => e.right[Validation[B, A]], a => Validation.success[B, A](a).left[E])
    )(_.failure[A])

  final def disjunctionIso[E1, E2, A1, A2]: PIso[Validation[E1, A1], Validation[E2, A2], E1 \/ A1, E2 \/ A2] =
    PIso[Validation[E1, A1], Validation[E2, A2], E1 \/ A1, E2 \/ A2](_.disjunction)(_.validation)
}
