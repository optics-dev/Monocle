package lens.syntax

import lens.Lens
import scalaz.Functor


class AppliedLens[A, B](from: A, lens: Lens[A, B]) extends AppliedSetter[A, B](from ,lens) {

  def get: B = lens.get(from)

  def lift[F[_] : Functor](f: B => F[B]):  F[A] = lens.lift(from, f)

  def >-[C](other: Lens[B,C]): AppliedLens[A,C] = new AppliedLens[A, C](from, lens >- other)
}




