package lens.impl

import lens.Traversal
import lens.util.{Identity, Constant}
import scala.language.higherKinds
import scalaz.{Monoid, Applicative}


trait HTraversal[A, B] extends Traversal[A,B] {
  protected def traversalFunction[F[_] : Applicative](lift: B => F[B], a: A): F[A]

  def get(from: A)(implicit ev: Monoid[B]): B = {
    val b2Fb: B => Constant[B, B] = { b: B => Constant(b)}
    traversalFunction[({type l[a] = Constant[B,a]})#l] (b2Fb, from).value
  }

  def modify(from: A, f: B => B): A =
    traversalFunction[Identity]({ b: B => Identity[B](f(b)) }, from).value

}
