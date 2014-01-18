package lens.impl

import lens.Traversal
import lens.util.{Identity, Constant}
import scala.language.higherKinds
import scalaz.Applicative
import scalaz.std.list._


trait HTraversal[A, B] extends Traversal[A,B] {
  protected def traversalFunction[F[_] : Applicative](lift: B => F[B], a: A): F[A]

  def get(from: A): List[B] = {
    val lift: B => Constant[List[B], B] = { b: B => Constant(List(b))}
    traversalFunction[({type l[a] = Constant[List[B],a]})#l](lift, from).value
  }

  def modify(from: A, f: B => B): A =
    traversalFunction[Identity]({ b: B => Identity[B](f(b)) }, from).value

}

object HTraversal {
  def compose[A, B, C](a2b: HTraversal[A, B], b2C: HTraversal[B, C]): HTraversal[A, C] = new HTraversal[A, C] {
    protected def traversalFunction[F[_] : Applicative](lift: (C) => F[C], a: A): F[A] =
      a2b.traversalFunction({b: B => b2C.traversalFunction(lift, b)}, a)
  }

}
