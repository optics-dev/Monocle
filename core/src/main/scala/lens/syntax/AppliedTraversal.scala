package lens.syntax

import lens.Traversal
import scalaz.{Monoid, Applicative}


class AppliedTraversal[A, B](from: A, traversal: Traversal[A, B]) extends AppliedSetter[A,B](from, traversal) {

  def get: List[B] = traversal.get(from)

  def lift[F[_] : Applicative](f: B => F[B]):  F[A] = traversal.lift[F](from, f)

  def fold(zero: B)(append: (B, B) => B): B = traversal.fold(from, zero)(append)

  def fold(implicit ev: Monoid[B]): B = traversal.fold(from)

}
