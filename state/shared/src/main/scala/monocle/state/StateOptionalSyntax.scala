package monocle.state

import monocle.POptional

import scalaz.{IndexedState, State}

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

  /** modify the value viewed through the Optional and return its *old* value, if there was one */
  def mod(f: A => B): IndexedState[S, T, Option[A]] =
    IndexedState(s => (optional.modify(f)(s), optional.getOption(s)))

  /** set the value viewed through the Optional and return its *old* value, if there was one */
  def assign(b: B): IndexedState[S, T, Option[A]] = mod(_ => b)

}