package monocle.state

import monocle.IPTraversal

import scalaz.{IndexedState, State}

trait StateITraversalSyntax {
  implicit def toStateITraversalOps[I, S, T, A, B](itraversal: IPTraversal[I, S, T, A, B]): StateITraversalOps[I, S, T, A, B] =
    new StateITraversalOps[I, S, T, A, B](itraversal)
}

final class StateITraversalOps[I, S, T, A, B](itraversal: IPTraversal[I, S, T, A, B]) {
  /** transforms a IPTraversal into a State */
  def toState: State[S, List[(I, A)]] =
    State(s => (s, itraversal.getAll(s)))

  /** alias for toState */
  def st: State[S, List[(I, A)]] =
    toState

  /** extracts the values viewed through the itraversal */
  def extract: State[S, List[(I, A)]] =
    toState

  /** extracts the values viewed through the itraversal and applied `f` over it */
  def extracts[B](f: List[(I, A)] => B): State[S, B] =
    extract.map(f)

  /** modify the values viewed through the itraversal and returns its *new* values */
  def mod(f: I => A => B): IndexedState[S, T, List[(I, B)]] =
    IndexedState(s => {
      val ias = itraversal.getAll(s)
      (itraversal.modify(f)(s), ias.map { case (i, a) => (i, f(i)(a)) })
    })

  /** modify the values viewed through the itraversal and returns its *old* values */
  def modo(f: I => A => B): IndexedState[S, T, List[(I, A)]] =
    toState.leftMap(itraversal.modify(f))

  /** modify the values viewed through the itraversal and ignores both values */
  def mod_(f: I => A => B): IndexedState[S, T, Unit] =
    IndexedState(s => (itraversal.modify(f)(s), ()))

  /** set the values viewed through the itraversal and returns its *new* values */
  def assign(b: B): IndexedState[S, T, List[(I, B)]] =
    mod(_ => _ => b)

  /** set the values viewed through the itraversal and returns its *old* values */
  def assigno(b: B): IndexedState[S, T, List[(I, A)]] =
    modo(_ => _ => b)

  /** set the values viewed through the itraversal and ignores both values */
  def assign_(b: B): IndexedState[S, T, Unit] =
    mod_(_ => _ => b)
}
