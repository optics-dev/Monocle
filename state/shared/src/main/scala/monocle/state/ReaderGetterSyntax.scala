package monocle.state

import monocle.Getter

import cats.data.Reader

trait ReaderGetterSyntax {
  implicit def toReaderGetterOps[S, A](getter: Getter[S, A]): ReaderGetterOps[S, A] =
    new ReaderGetterOps[S, A](getter)
}

final class ReaderGetterOps[S, A](getter: Getter[S, A]) {
  /** transforms a Getter into a Reader */
  def toReader: Reader[S, A] =
    Reader(getter.get)

  /** alias for toReader */
  def rd: Reader[S, A] =
    toReader

  /** extracts the value viewed through the getter */
  def ask: Reader[S, A] =
    toReader

  /** extracts the value viewed through the getter and applies `f` over it */
  def asks[B](f: A => B): Reader[S, B] =
    ask.map(f)
}
