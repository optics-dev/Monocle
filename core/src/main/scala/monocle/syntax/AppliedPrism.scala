package monocle.syntax

import monocle.{Prism, Traversal}


trait AppliedPrism[S, T, A, B] extends AppliedTraversal[S, T, A, B]  { self =>

  def _prism: Prism[S, T, A, B]

  def _traversal: Traversal[S, T, A, B] = _prism

  def reverseGet(from: B): T = _prism.reverseGet(from)

  def getOption(from: S): Option[A] = _prism.getOption(from)

  def oo[C, D](other: Prism[A, B, C, D]): AppliedPrism[S, T, C, D] = new AppliedPrism[S, T, C, D] {
    val _prism: Prism[S, T, C, D] = self._prism compose other
    val from: S = self.from
  }
}