package monocle.std

import monocle.{Prism, Iso, PIso, PPrism}

import scalaz.syntax.either._
import scalaz.syntax.validation._
import scalaz.{Validation, \/}

object validation extends ValidationFunctions

trait ValidationFunctions {
  final def pSuccess[E, A, B]: PPrism[Validation[E, A], Validation[E, B], A, B] =
    PPrism[Validation[E, A], Validation[E, B], A, B](
      _.fold(e => Validation.failure[E, B](e).left[A], a => a.right[Validation[E, B]])
    )(_.success[E])

  final def success[E, A]: Prism[Validation[E, A], A] =
    pSuccess[E, A, A]

  final def pFailure[E, A, F]: PPrism[Validation[E, A], Validation[F, A], E, F] =
    PPrism[Validation[E, A], Validation[F, A], E, F](
      _.fold(e => e.right[Validation[F, A]], a => Validation.success[F, A](a).left[E])
    )(_.failure[A])

  final def failure[E, A]: Prism[Validation[E, A], E] =
    pFailure[E, A, E]

  final def pValidationToDisjunction[E1, E2, A1, A2]: PIso[Validation[E1, A1], Validation[E2, A2], E1 \/ A1, E2 \/ A2] =
    PIso[Validation[E1, A1], Validation[E2, A2], E1 \/ A1, E2 \/ A2](_.disjunction)(_.validation)

  final def validationToDisjunction[E, A]: Iso[Validation[E, A], E \/ A] =
    pValidationToDisjunction[E, E, A, A]
}
