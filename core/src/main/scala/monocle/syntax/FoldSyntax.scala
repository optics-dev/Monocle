package monocle.syntax

import monocle.Fold
import scalaz.Monoid

private[syntax] trait FoldSyntax {
  implicit def toPartialApplyFoldOps[S](value: S): PartialApplyFoldOps[S] = new PartialApplyFoldOps(value)
}


private[syntax] trait PartialApplyFold[S, A] { self =>
  def from: S
  def _fold: Fold[S, A]

  def foldMap[B: Monoid](f: A => B): B = _fold.foldMap(from)(f)

  def fold(implicit ev: Monoid[A]): A = _fold.fold(from)

  def getAll: List[A] = _fold.getAll(from)

  def headOption: Option[A] = _fold.headOption(from)

  def exist(p: A => Boolean): Boolean = _fold.exist(from)(p)

  def all(p: A => Boolean): Boolean = _fold.all(from)(p)

  def composeFold[B](other: Fold[A, B]): PartialApplyFold[S, B] = new PartialApplyFold[S, B] {
    val from: S = self.from
    val _fold: Fold[S, B] = self._fold compose other
  }
}

private[syntax] final class PartialApplyFoldOps[S](value: S) {
  def partialApplyFold[A](f: Fold[S, A]): PartialApplyFold[S, A] = new PartialApplyFold[S, A] {
    val from: S = value
    def _fold: Fold[S, A] = f
  }
}