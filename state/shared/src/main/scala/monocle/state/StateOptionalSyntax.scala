package monocle.state

import monocle.POptional

import scalaz.{IndexedState, State}

trait StateOptionalSyntax {
  implicit def toStateOptionalOps[S, T, A, B](optional: POptional[S, T, A, B]): StateOptionalOps[S, T, A, B] =
    new StateOptionalOps[S, T, A, B](optional)
}

final class StateOptionalOps[S, T, A, B](private val optional: POptional[S, T, A, B]) extends AnyVal {
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

  /** modify the value viewed through the Optional and return its *new* value, if there is one */
  def mod(f: A => B): IndexedState[S, T, Option[B]] =
    modo(f).map(_.map(f))

  /** modify the value viewed through the Optional and return its *old* value, if there was one */
  def modo(f: A => B): IndexedState[S, T, Option[A]] =
    IndexedState(s => (optional.modify(f)(s), optional.getOption(s)))

  /** modify the value viewed through the Optional and ignores both values */
  def mod_(f: A => B): IndexedState[S, T, Unit] =
    IndexedState(s => (optional.modify(f)(s), ()))

  /** set the value viewed through the Optional and returns its *new* value */
  def assign(b: B): IndexedState[S, T, Option[B]] =
    mod(_ => b)

  /** set the value viewed through the Optional and return its *old* value, if there was one */
  def assigno(b: B): IndexedState[S, T, Option[A]] =
    modo(_ => b)

  /** set the value viewed through the Optional and ignores both values */
  def assign_(b: B): IndexedState[S, T, Unit] =
    mod_(_ => b)
}
