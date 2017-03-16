package monocle.state

import monocle.PLens

import cats.data.State

trait StateLensSyntax {
  implicit def toStateLensOps[S, T, A, B](lens: PLens[S, T, A, B]): StateLensOps[S, T, A, B] =
    new StateLensOps[S, T, A, B](lens)
}

final class StateLensOps[S, T, A, B](lens: PLens[S, T, A, B]) {
  /** transforms a PLens into a State */
  def toState: State[S, A] =
    State(s => (s, lens.get(s)))

  /** alias for toState */
  def st: State[S, A] =
    toState

  /** extracts the value viewed through the lens */
  def extract: State[S, A] =
    toState

  /** extracts the value viewed through the lens and applies `f` over it */
  def extracts[B](f: A => B): State[S, B] =
    extract.map(f)
}
