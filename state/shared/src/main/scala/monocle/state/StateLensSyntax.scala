package monocle.state

import monocle.PLens

import scalaz.{IndexedState, State}
import scalaz.syntax.functor._

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

  /** modify the value viewed through the lens and returns its *new* value */
  def mod(f: A => B): IndexedState[S, T, B] =
    IndexedState(s => {
      val a = lens.get(s)
      val b = f(a)
      (lens.set(b)(s), b)
    })

  /** modify the value viewed through the lens and returns its *old* value */
  def modo(f: A => B): IndexedState[S, T, A] =
    toState.leftMap(lens.modify(f))

  /** modify the value viewed through the lens and ignores both values */
  def mod_(f: A => B): IndexedState[S, T, Unit] =
    IndexedState(s => (lens.modify(f)(s), ()))

  /** set the value viewed through the lens and returns its *new* value */
  def assign(b: B): IndexedState[S, T, B] =
    mod(_ => b)

  /** set the value viewed through the lens and returns its *old* value */
  def assigno(b: B): IndexedState[S, T, A] =
    modo(_ => b)

  /** set the value viewed through the lens and ignores both values */
  def assign_(b: B): IndexedState[S, T, Unit] =
    mod_(_ => b)
}
