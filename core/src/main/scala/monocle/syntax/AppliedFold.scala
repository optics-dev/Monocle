package monocle.syntax

import monocle.Fold
import scalaz.Monoid

trait AppliedFold[S, A] { self =>
  val from: S

  def _fold: Fold[S, A]

  def foldMap[B: Monoid](f: A => B): B = _fold.foldMap(from)(f)

  def fold(implicit ev: Monoid[A]): A = _fold.fold(from)

  def toListOf: List[A] = _fold.toListOf(from)

  def headOption: Option[A] = _fold.headOption(from)

  def exist(p: A => Boolean): Boolean = _fold.exist(from)(p)

  def all(p: A => Boolean): Boolean = _fold.all(from)(p)

  def oo[B](other: Fold[A, B]): AppliedFold[S, B] = new AppliedFold[S, B] {
    val from: S = self.from
    val _fold: Fold[S, B] = self._fold compose other
  }
}
