package monocle.std

import monocle.{Prism, Iso, PIso, PPrism}

import cats.data.{Validated => Validation}
import cats.syntax.either._
import cats.syntax.validated._
import scala.{Either => \/}

object validation extends ValidationOptics

trait ValidationOptics {
  final def pSuccess[E, A, B]: PPrism[Validation[E, A], Validation[E, B], A, B] =
    PPrism[Validation[E, A], Validation[E, B], A, B](
      _.fold(e => Validation.invalid[E, B](e).asLeft[A], a => a.asRight[Validation[E, B]])
    )(_.valid[E])

  final def success[E, A]: Prism[Validation[E, A], A] =
    pSuccess[E, A, A]

  final def pFailure[E, A, F]: PPrism[Validation[E, A], Validation[F, A], E, F] =
    PPrism[Validation[E, A], Validation[F, A], E, F](
      _.fold(e => e.asRight[Validation[F, A]], a => Validation.valid[F, A](a).asLeft[E])
    )(_.invalid[A])

  final def failure[E, A]: Prism[Validation[E, A], E] =
    pFailure[E, A, E]

  final def pValidationToDisjunction[E1, E2, A1, A2]: PIso[Validation[E1, A1], Validation[E2, A2], E1 \/ A1, E2 \/ A2] =
    PIso[Validation[E1, A1], Validation[E2, A2], E1 \/ A1, E2 \/ A2](_.toEither)(_.toValidated)

  final def validationToDisjunction[E, A]: Iso[Validation[E, A], E \/ A] =
    pValidationToDisjunction[E, E, A, A]
}
