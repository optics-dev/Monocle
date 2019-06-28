package monocle.std

import monocle.{Prism, Iso, PIso, PPrism}

import cats.data.Validated
import cats.syntax.either._
import cats.syntax.validated._

object validated extends ValidatedOptics

trait ValidatedOptics {
  final def pSuccess[E, A, B]: PPrism[Validated[E, A], Validated[E, B], A, B] =
    PPrism[Validated[E, A], Validated[E, B], A, B](
      _.fold(e => Validated.invalid[E, B](e).asLeft[A], a => a.asRight[Validated[E, B]])
    )(_.valid[E])

  final def success[E, A]: Prism[Validated[E, A], A] =
    pSuccess[E, A, A]

  final def pFailure[E, A, F]: PPrism[Validated[E, A], Validated[F, A], E, F] =
    PPrism[Validated[E, A], Validated[F, A], E, F](
      _.fold(e => e.asRight[Validated[F, A]], a => Validated.valid[F, A](a).asLeft[E])
    )(_.invalid[A])

  final def failure[E, A]: Prism[Validated[E, A], E] =
    pFailure[E, A, E]

  final def pValidatedToDisjunction[E1, E2, A1, A2]: PIso[Validated[E1, A1], Validated[E2, A2], Either[E1, A1], Either[E2, A2]] =
    PIso[Validated[E1, A1], Validated[E2, A2], Either[E1, A1], Either[E2, A2]](_.toEither)(_.toValidated)

  final def validationToDisjunction[E, A]: Iso[Validated[E, A], Either[E, A]] =
    pValidatedToDisjunction[E, E, A, A]
}
