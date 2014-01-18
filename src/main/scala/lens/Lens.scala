package lens

import scala.language.higherKinds
import scalaz.Functor
import util.Identity


trait Lens[A,B] {

  def get(from: A): B

  def lift[F[_] : Functor](from: A, f: B => F[B]):  F[A]

  // default implementation of set using modify
  def set(from: A, newValue: B): A = modify(from, _ => newValue)

  // default implementation of modify using lift
  def modify(from: A, f: B => B): A = lift(from, { b : B => Identity(f(b)) } ).value

  def >-[C](other: Lens[B,C]): Lens[A,C] = Lens.compose(this, other)
  def >-[C](other: Traversal[B,C]): Traversal[A,C] = Traversal.compose(this, other)
}


object Lens {
  def compose[A, B, C](a2b: Lens[A, B], b2C: Lens[B, C]): Lens[A, C] =  new Lens[A, C] {
    def get(from: A): C = b2C.get(a2b.get(from))

    def lift[F[_] : Functor](from: A, f: C => F[C]): F[A] = a2b.lift(from, b2C.lift(_, f))
  }
}