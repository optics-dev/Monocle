package monocle.state

import monocle.PTraversal

import scalaz.{IndexedState, State}

trait StateTraversalSyntax {
  implicit def toStateTraversalOps[S, T, A, B](traversal: PTraversal[S, T, A, B]): StateTraversalOps[S, T, A, B] =
    new StateTraversalOps[S, T, A, B](traversal)
}

final class StateTraversalOps[S, T, A, B](private val traversal: PTraversal[S, T, A, B]) extends AnyVal {
  /** transforms a PTraversal into a State */
  def toState: State[S, List[A]] =
    State(s => (s, traversal.getAll(s)))

  /** alias for toState */
  def st: State[S, List[A]] =
    toState

  /** extracts the values viewed through the traversal */
  def extract: State[S, List[A]] =
    toState

  /** extracts the values viewed through the traversal and applied `f` over it */
  def extracts[B](f: List[A] => B): State[S, B] =
    extract.map(f)

  /** modify the values viewed through the traversal and returns its *new* values */
  def mod(f: A => B): IndexedState[S, T, List[B]] =
    IndexedState(s => {
      val as = traversal.getAll(s)
      (traversal.modify(f)(s), as.map(f))
    })

  /** modify the values viewed through the traversal and returns its *old* values */
  def modo(f: A => B): IndexedState[S, T, List[A]] =
    toState.leftMap(traversal.modify(f))

  /** modify the values viewed through the traversal and ignores both values */
  def mod_(f: A => B): IndexedState[S, T, Unit] =
    IndexedState(s => (traversal.modify(f)(s), ()))

  /** set the values viewed through the traversal and returns its *new* values */
  def assign(b: B): IndexedState[S, T, List[B]] =
    mod(_ => b)

  /** set the values viewed through the traversal and returns its *old* values */
  def assigno(b: B): IndexedState[S, T, List[A]] =
    modo(_ => b)

  /** set the values viewed through the traversal and ignores both values */
  def assign_(b: B): IndexedState[S, T, Unit] =
    mod_(_ => b)
}
