package monocle.state

import monocle.Getter

import scalaz.State

trait StateGetterSyntax {
  implicit def toStateGetterOps[S, A](getter: Getter[S, A]): StateGetterOps[S, A] =
    new StateGetterOps[S, A](getter)
}

final class StateGetterOps[S, A](private val getter: Getter[S, A]) extends AnyVal {
  /** transforms a Getter into a State */
  def toState: State[S, A] =
    State(s => (s, getter.get(s)))

  /** alias for toState */
  def st: State[S, A] =
    toState

  /** extracts the value viewed through the getter */
  def extract: State[S, A] =
    toState

  /** extracts the value viewed through the getter and applies `f` over it */
  def extracts[B](f: A => B): State[S, B] =
    extract.map(f)
}
