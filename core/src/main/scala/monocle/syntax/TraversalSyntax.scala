package monocle.syntax

import monocle.Traversal


final class TraversalOps[S, T, A, B](val self: Traversal[S, T, A, B]) {
  def oo[C, D](other: Traversal[A, B, C, D]): Traversal[S, T, C, D] = self compose other
}

trait ToTraversalOps {
  implicit def toTraversalSyntaxOps[S, T, A, B](traversal: Traversal[S, T, A, B]) = new TraversalOps[S, T, A, B](traversal)
}

trait TraversalSyntax[S, T, A, B] {
  implicit def toTraversalSyntaxOps(traversal: Traversal[S, T, A, B]) = new TraversalOps[S, T, A, B](traversal)
}