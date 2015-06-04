package monocle.syntax

import monocle.PLens

import scalaz.{IndexedState, State}

object state extends StateSyntax

trait StateSyntax {
  implicit def toStateLensOps[S, T, A, B](lens: PLens[S, T, A, B]): StateLensOps[S, T, A, B] =
    new StateLensOps[S, T, A, B](lens)
}

final class StateLensOps[S, T, A, B](lens: PLens[S, T, A, B]) {
  def toState: State[S, A] = State(s => (s, lens.get(s)))

  /** alias for toState */
  def st: State[S, A] = toState

  def updateState(f: A => B): IndexedState[S, T, A] =
    toState.leftMap(lens.modify(f))

  /** alias for updateState */
  def ~=(f: A => B): IndexedState[S, T, A] =
    updateState(f)

  def assign(value: B): IndexedState[S, T, A] =
    toState.leftMap(lens.set(value))

  /** alias for assign */
  def :=(value: B): IndexedState[S, T, A] = assign(value)
}