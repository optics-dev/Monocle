package monocle.state

import cats.{Eval, Now}
import monocle.POptional
import cats.data.{IndexedStateT, State}

trait StateOptionalSyntax {
  @deprecated("no replacement", since = "3.0.0-M1")
  implicit def toStateOptionalOps[S, T, A, B](optional: POptional[S, T, A, B]): StateOptionalOps[S, T, A, B] =
    new StateOptionalOps[S, T, A, B](optional)
}

@deprecated("no replacement", since = "3.0.0-M1")
final class StateOptionalOps[S, T, A, B](private val optional: POptional[S, T, A, B]) extends AnyVal {

  /** transforms a POptional into a State */
  @deprecated("no replacement", since = "3.0.0-M1")
  def toState: State[S, Option[A]] =
    State(s => (s, optional.getOption(s)))

  /** alias for toState */
  @deprecated("no replacement", since = "3.0.0-M1")
  def st: State[S, Option[A]] =
    toState

  /** extracts the value viewed through the optional */
  @deprecated("no replacement", since = "3.0.0-M1")
  def extract: State[S, Option[A]] =
    toState

  /** extracts the value viewed through the optional and applies `f` over it */
  @deprecated("no replacement", since = "3.0.0-M1")
  def extracts[B](f: A => B): State[S, Option[B]] =
    extract.map(_.map(f))

  /** modify the value viewed through the Optional and return its *new* value, if there is one */
  @deprecated("no replacement", since = "3.0.0-M1")
  def mod(f: A => B): IndexedStateT[Eval, S, T, Option[B]] =
    modo(f).map(_.map(f))

  /** modify the value viewed through the Optional and return its *old* value, if there was one */
  @deprecated("no replacement", since = "3.0.0-M1")
  def modo(f: A => B): IndexedStateT[Eval, S, T, Option[A]] =
    IndexedStateT(s => Now((optional.modify(f)(s), optional.getOption(s))))

  /** modify the value viewed through the Optional and ignores both values */
  @deprecated("no replacement", since = "3.0.0-M1")
  def mod_(f: A => B): IndexedStateT[Eval, S, T, Unit] =
    IndexedStateT(s => Now((optional.modify(f)(s), ())))

  /** set the value viewed through the Optional and returns its *new* value */
  @deprecated("no replacement", since = "3.0.0-M1")
  def assign(b: B): IndexedStateT[Eval, S, T, Option[B]] =
    mod(_ => b)

  /** set the value viewed through the Optional and return its *old* value, if there was one */
  @deprecated("no replacement", since = "3.0.0-M1")
  def assigno(b: B): IndexedStateT[Eval, S, T, Option[A]] =
    modo(_ => b)

  /** set the value viewed through the Optional and ignores both values */
  @deprecated("no replacement", since = "3.0.0-M1")
  def assign_(b: B): IndexedStateT[Eval, S, T, Unit] =
    mod_(_ => b)
}
