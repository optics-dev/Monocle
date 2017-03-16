package monocle.state

import monocle.POptional

import cats.data.State

trait StateOptionalSyntax {
  implicit def toStateOptionalOps[S, T, A, B](optional: POptional[S, T, A, B]): StateOptionalOps[S, T, A, B] =
    new StateOptionalOps[S, T, A, B](optional)
}

final class StateOptionalOps[S, T, A, B](optional: POptional[S, T, A, B]) {
  /** transforms a POptional into a State */
  def toState: State[S, Option[A]] =
    State(s => (s, optional.getOption(s)))

  /** alias for toState */
  def st: State[S, Option[A]] =
    toState

    /** extracts the value viewed through the optional */
  def extract: State[S, Option[A]] =
    toState

  /** extracts the value viewed through the optional and applies `f` over it */
  def extracts[B](f: A => B): State[S, Option[B]] =
    extract.map(_.map(f))
}
