package monocle.state

import monocle.Getter

import cats.data.State

trait StateGetterSyntax {
  @deprecated("no replacement", since = "3.0.0-M1")
  implicit def toStateGetterOps[S, A](getter: Getter[S, A]): StateGetterOps[S, A] =
    new StateGetterOps[S, A](getter)
}

@deprecated("no replacement", since = "3.0.0-M1")
final class StateGetterOps[S, A](private val getter: Getter[S, A]) extends AnyVal {

  /** transforms a Getter into a State */
  @deprecated("no replacement", since = "3.0.0-M1")
  def toState: State[S, A] =
    State(s => (s, getter.get(s)))

  /** alias for toState */
  @deprecated("no replacement", since = "3.0.0-M1")
  def st: State[S, A] =
    toState

  /** extracts the value viewed through the getter */
  @deprecated("no replacement", since = "3.0.0-M1")
  def extract: State[S, A] =
    toState

  /** extracts the value viewed through the getter and applies `f` over it */
  @deprecated("no replacement", since = "3.0.0-M1")
  def extracts[B](f: A => B): State[S, B] =
    extract.map(f)
}
