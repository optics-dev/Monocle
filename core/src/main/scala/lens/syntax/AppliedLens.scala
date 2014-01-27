package lens.syntax

import lens.{Traversal, Lens}
import scalaz.Functor

case class AppliedLens[A, B](from: A, lens: Lens[A, B]) {

  def get: B = lens.get(from)

  def set(newValue: B): A  = lens.set(from, newValue)

  def modify(f: B => B): A = lens.modify(from, f)

  def lift[F[_] : Functor](f: B => F[B]):  F[A] = lens.lift(from, f)

  def >-[C](other: Lens[B,C])     : AppliedLens[A,C]      = AppliedLens[A, C](from, lens >- other)
  def >-[C](other: Traversal[B,C]): AppliedTraversal[A,C] = AppliedTraversal[A, C](from, lens >- other)
}




