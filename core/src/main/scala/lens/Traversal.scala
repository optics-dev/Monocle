package lens

import lens.impl.HTraversal
import lens.util.Identity
import scalaz.{Traverse, Applicative, Monoid}

trait Traversal[A, B] {

  def get(from: A): List[B]

  def lift[F[_] : Applicative](from: A, f: B => F[B]):  F[A]

  def fold(from: A, zero: B)(append: (B, B) => B): B = get(from).foldRight(zero)(append)

  def fold(from: A)(implicit ev: Monoid[B]): B = fold(from, ev.zero){ case (b1, b2) => ev.append(b1, b2) }

  def set(from: A, newValue: B): A  = modify(from, _ => newValue)

  def modify(from: A, f: B => B): A = lift(from, { b : B => Identity(f(b)) }).value

  def >-[C](other: Traversal[B,C]): Traversal[A,C] = Traversal.compose(this, other)
  def >-[C](other:      Lens[B,C]): Traversal[A,C] = Traversal.compose(this, other)
}


object Traversal {

  def apply[T[_]: Traverse, A]: HTraversal[T[A], A] = new HTraversal[T[A], A] {
    protected def traversalFunction[F[_] : Applicative](lift: A => F[A], from: T[A]): F[T[A]] =
      Traverse[T].traverse(from)(lift)
  }

  def compose[A, B, C](a2b: Traversal[A, B], b2C: Traversal[B, C]): Traversal[A, C] = new Traversal[A, C] {
    def get(from: A): List[C] = a2b.get(from) flatMap b2C.get
    def lift[F[_] : Applicative](from: A, f: C => F[C]): F[A] = a2b.lift(from, b2C.lift(_, f))
  }

  def compose[A, B, C](a2b: Traversal[A, B], b2C: Lens[B, C]): Traversal[A, C] = new Traversal[A, C] {
    def get(from: A): List[C] = a2b.get(from) map b2C.get
    def lift[F[_] : Applicative](from: A, f: C => F[C]): F[A] = a2b.lift(from, b2C.lift(_, f))
  }


}
