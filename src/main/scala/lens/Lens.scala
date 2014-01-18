package lens

import scalaz.Functor
import util.Identity


trait Lens[A,B] extends Getter[A,B] with Setter[A, B] with Modifier[A, B] with Lift[A, B] {
  // default implementation of set using modify
  def set(from: A, newValue: B): A = modify(from, _ => newValue)

  // default implementation of modify using lift
  def modify(from: A, f: B => B): A = lift(from, { b : B => Identity(f(b)) } ).value

  def >-[C](other: Lens[B,C]): Lens[A,C] = Lens.compose(this, other)
}

trait Getter[A, B]{
  def get(from: A): B
}

trait Setter[A,B] {
  def set(from: A, newValue: B): A
}

trait Modifier[A, B] extends Setter[A, B] {
  def modify(from: A, f: B => B): A
}

trait Lift[A, B] {
  def lift[F[_] : Functor](from: A, f: B => F[B]):  F[A]
}

object Lens {
  def compose[A, B, C](a2b: Lens[A, B], b2C: Lens[B, C]): Lens[A, C] =  new Lens[A, C] {
    def get(from: A): C = b2C.get(a2b.get(from))

    def lift[F[_] : Functor](from: A, f: C => F[C]): F[A] = a2b.lift(from, b2C.lift(_, f))
  }
}