package lens.syntax

import lens.{Lens, Traversal}
import scalaz.{Monoid, Applicative}

case class AppliedTraversal[A, B](from: A, traversal: Traversal[A, B]) {

  def get: List[B] = traversal.get(from)

  def set(newValue: B): A  = traversal.set(from, newValue)

  def modify(f: B => B): A = traversal.modify(from, f)

  def lift[F[_] : Applicative](f: B => F[B]):  F[A] = traversal.lift[F](from, f)

  def fold(zero: B)(append: (B, B) => B): B = traversal.fold(from, zero)(append)

  def fold(implicit ev: Monoid[B]): B = traversal.fold(from)

  def >-[C](other: Lens[B,C]): AppliedTraversal[A,C] = new AppliedTraversal[A, C](from, traversal >- other)
  def >-[C](other: Traversal[B,C]): AppliedTraversal[A,C] = new AppliedTraversal[A, C](from, traversal >- other)

}
