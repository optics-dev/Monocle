package lens.syntax

import lens.Lens
import scalaz.Functor


case class AppliedLens[A, B](from: A, lens: Lens[A, B]) {
  def get: B = lens.get(from)

  def lift[F[_] : Functor](f: B => F[B]):  F[A] = lens.lift(from, f)

  def set(newValue: B): A = lens.set(from, newValue)

  def modify(f: B => B): A = lens.modify(from, f)
}




