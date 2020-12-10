package monocle.state

import monocle.PTraversal
import cats._
import cats.data._
import cats.implicits._

trait StateTraversalSyntax {
  @deprecated("no replacement", since = "3.0.0-M1")
  implicit def toStateTraversalOps[S, T, A, B](traversal: PTraversal[S, T, A, B]): StateTraversalOps[S, T, A, B] =
    new StateTraversalOps[S, T, A, B](traversal)
}

@deprecated("no replacement", since = "3.0.0-M1")
final class StateTraversalOps[S, T, A, B](private val traversal: PTraversal[S, T, A, B]) extends AnyVal {

  /** transforms a PTraversal into a State */
  @deprecated("no replacement", since = "3.0.0-M1")
  def toState: State[S, List[A]] =
    State(s => (s, traversal.getAll(s)))

  /** alias for toState */
  @deprecated("no replacement", since = "3.0.0-M1")
  def st: State[S, List[A]] =
    toState

  /** extracts the values viewed through the traversal */
  @deprecated("no replacement", since = "3.0.0-M1")
  def extract: State[S, List[A]] =
    toState

  /** extracts the values viewed through the traversal and applied `f` over it */
  @deprecated("no replacement", since = "3.0.0-M1")
  def extracts(f: List[A] => B): State[S, B] =
    extract.map(f)

  /** modify the values viewed through the traversal and returns its *new* values */
  @deprecated("no replacement", since = "3.0.0-M1")
  def mod[F](f: A => B): IndexedState[S, T, List[B]] =
    IndexedState[S, T, List[B]] { s =>
      val as = traversal.getAll(s)
      (traversal.modifyF(f: A => Id[B])(s), as.map(f))
    }

  @deprecated("no replacement", since = "3.0.0-M1")
  def modF[F[_]: Applicative](f: A => F[B]): IndexedStateT[F, S, T, List[B]] =
    IndexedStateT { s =>
      val as = traversal.getAll(s)
      (traversal.modifyF(f)(s), as.traverse(f)).tupled
    }

  /** modify the values viewed through the traversal and returns its *old* values */
  @deprecated("no replacement", since = "3.0.0-M1")
  def modo(f: A => B): IndexedState[S, T, List[A]] =
    Bifunctor[IndexedStateT[Eval, S, *, *]].leftMap(st)(traversal.modify(f))

  /** modify the values viewed through the traversal and ignores both values */
  @deprecated("no replacement", since = "3.0.0-M1")
  def mod_(f: A => B): IndexedState[S, T, Unit] =
    IndexedState(s => (traversal.modify(f)(s), ()))

  /** set the values viewed through the traversal and returns its *new* values */
  @deprecated("no replacement", since = "3.0.0-M1")
  def assign(b: B): IndexedState[S, T, List[B]] =
    mod(_ => b)

  /** set the values viewed through the traversal and returns its *old* values */
  @deprecated("no replacement", since = "3.0.0-M1")
  def assigno(b: B): IndexedState[S, T, List[A]] =
    modo(_ => b)

  /** set the values viewed through the traversal and ignores both values */
  @deprecated("no replacement", since = "3.0.0-M1")
  def assign_(b: B): IndexedState[S, T, Unit] =
    mod_(_ => b)
}
