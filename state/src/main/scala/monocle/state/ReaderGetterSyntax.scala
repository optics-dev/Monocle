package monocle.state

import monocle.Getter

import cats.data.Reader

trait ReaderGetterSyntax {
  @deprecated("no replacement", since = "3.0.0-M1")
  implicit def toReaderGetterOps[S, A](getter: Getter[S, A]): ReaderGetterOps[S, A] =
    new ReaderGetterOps[S, A](getter)
}

@deprecated("no replacement", since = "3.0.0-M1")
final class ReaderGetterOps[S, A](private val getter: Getter[S, A]) extends AnyVal {

  /** transforms a Getter into a Reader */
  @deprecated("no replacement", since = "3.0.0-M1")
  def toReader: Reader[S, A] =
    Reader(getter.get)

  /** alias for toReader */
  @deprecated("no replacement", since = "3.0.0-M1")
  def rd: Reader[S, A] =
    toReader

  /** extracts the value viewed through the getter */
  @deprecated("no replacement", since = "3.0.0-M1")
  def ask: Reader[S, A] =
    toReader

  /** extracts the value viewed through the getter and applies `f` over it */
  @deprecated("no replacement", since = "3.0.0-M1")
  def asks[B](f: A => B): Reader[S, B] =
    ask.map(f)
}
