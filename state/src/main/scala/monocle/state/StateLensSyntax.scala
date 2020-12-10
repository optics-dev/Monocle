package monocle.state

import cats.{Eval, Now}
import monocle.PLens
import cats.data.{IndexedStateT, State}

trait StateLensSyntax {
  @deprecated("no replacement", since = "3.0.0-M1")
  implicit def toStateLensOps[S, T, A, B](lens: PLens[S, T, A, B]): StateLensOps[S, T, A, B] =
    new StateLensOps[S, T, A, B](lens)
}

@deprecated("no replacement", since = "3.0.0-M1")
final class StateLensOps[S, T, A, B](private val lens: PLens[S, T, A, B]) extends AnyVal {

  /** transforms a PLens into a State */
  @deprecated("no replacement", since = "3.0.0-M1")
  def toState: State[S, A] =
    State(s => (s, lens.get(s)))

  /** alias for toState */
  @deprecated("no replacement", since = "3.0.0-M1")
  def st: State[S, A] =
    toState

  /** extracts the value viewed through the lens */
  @deprecated("no replacement", since = "3.0.0-M1")
  def extract: State[S, A] =
    toState

  /** extracts the value viewed through the lens and applies `f` over it */
  @deprecated("no replacement", since = "3.0.0-M1")
  def extracts[B](f: A => B): State[S, B] =
    extract.map(f)

  /** modify the value viewed through the lens and returns its *new* value */
  @deprecated("no replacement", since = "3.0.0-M1")
  def mod(f: A => B): IndexedStateT[Eval, S, T, B] =
    IndexedStateT { s =>
      val a = lens.get(s)
      val b = f(a)
      Now((lens.replace(b)(s), b))
    }

  /** modify the value viewed through the lens and returns its *old* value */
  @deprecated("no replacement", since = "3.0.0-M1")
  def modo(f: A => B): IndexedStateT[Eval, S, T, A] =
    toState.bimap(lens.modify(f), identity)

  /** modify the value viewed through the lens and ignores both values */
  @deprecated("no replacement", since = "3.0.0-M1")
  def mod_(f: A => B): IndexedStateT[Eval, S, T, Unit] =
    IndexedStateT(s => Now((lens.modify(f)(s), ())))

  /** set the value viewed through the lens and returns its *new* value */
  @deprecated("no replacement", since = "3.0.0-M1")
  def assign(b: B): IndexedStateT[Eval, S, T, B] =
    mod(_ => b)

  /** set the value viewed through the lens and returns its *old* value */
  @deprecated("no replacement", since = "3.0.0-M1")
  def assigno(b: B): IndexedStateT[Eval, S, T, A] =
    modo(_ => b)

  /** set the value viewed through the lens and ignores both values */
  @deprecated("no replacement", since = "3.0.0-M1")
  def assign_(b: B): IndexedStateT[Eval, S, T, Unit] =
    mod_(_ => b)
}
