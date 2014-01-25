package lens.impl

import lens.Traversal
import lens.util.Constant
import scala.language.higherKinds
import scalaz.Applicative

trait HTraversal[A, B] extends Traversal[A,B] {
  protected def traversalFunction[F[_] : Applicative](lift: B => F[B], a: A): F[A]

  def get(from: A): List[B] = {
    import scalaz.std.list._
    val lift: B => Constant[List[B], B] = { b: B => Constant(List(b))}
    traversalFunction[({type l[a] = Constant[List[B],a]})#l](lift, from).value
  }

  def lift[F[_] : Applicative](from: A, f: B => F[B]): F[A] = traversalFunction(f, from)
}

object HTraversal {
  def compose[A, B, C](a2b: HTraversal[A, B], b2C: HTraversal[B, C]): HTraversal[A, C] = new HTraversal[A, C] {
    protected def traversalFunction[F[_] : Applicative](lift: (C) => F[C], a: A): F[A] =
      a2b.traversalFunction({b: B => b2C.traversalFunction(lift, b)}, a)
  }

}
