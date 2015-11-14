package monocle

import scalaz._
import scalaz.std.stream._

/**
  * [[Plated]] is a type-class for types which can extract their immediate
  * self-similar children.
  *
  * @tparam A the parent and child type of a [[Plated]]
  */
abstract class Plated[A] extends Serializable { self =>
  def plate: Traversal[A, A]
}

object Plated {

  def plate[A](implicit P: Plated[A]): Traversal[A, A] = P.plate
  @inline def children[A: Plated](a: A): List[A] = plate[A].getAll(a)
  def universe[A: Plated](a: A): Stream[A] = {
    val fold = plate[A].asFold
    def go(b: A): Stream[A] = b #:: fold.foldMap[Stream[A]](go)(b)
    go(a)
  }
  def rewrite[A: Plated](f: A => Option[A])(a: A): A =
    rewriteOf(plate[A].asSetter)(f)(a)
  def rewriteOf[A](l: Setter[A, A])(f: A => Option[A])(a: A): A = {
    def go(b: A): A = {
      val c = transformOf(l)(go)(b)
      f(c).fold(c)(go)
    }
    go(a)
  }
  def transform[A: Plated](f: A => A)(a: A): A =
    transformOf(plate[A].asSetter)(f)(a)
  def transformOf[A](l: Setter[A, A])(f: A => A)(a: A): A =
    l.modify(b => transformOf(l)(f)(f(b)))(a)

}
