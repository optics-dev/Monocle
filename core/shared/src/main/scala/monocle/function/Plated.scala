package monocle.function

import monocle.{Setter, Traversal}

import scala.annotation.implicitNotFound
import scalaz.std.stream._

/**
  * [[Plated]] is a type-class for types which can extract their immediate
  * self-similar children.
  *
  * @tparam A the parent and child type of a [[Plated]]
  */
@implicitNotFound("Could not find an instance of Plated[${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
abstract class Plated[A] extends Serializable { self =>
  def plate: Traversal[A, A]
}

object Plated extends PlatedFunctions

trait PlatedFunctions {

  /** [[Traversal]] of immediate self-similar children */
  def plate[A](implicit P: Plated[A]): Traversal[A, A] = P.plate

  /** get the immediate self-similar children of a target */
  @inline def children[A: Plated](a: A): List[A] = plate[A].getAll(a)

  /** get all transitive self-similar elements of a target, including itself */
  def universe[A: Plated](a: A): Stream[A] = {
    val fold = plate[A].asFold
    def go(b: A): Stream[A] = b #:: fold.foldMap[Stream[A]](go)(b)
    go(a)
  }

  /**
    * rewrite a target by applying a rule as often as possible until it reaches
    * a fixpoint (this is an infinite loop if there is no fixpoint)
    */
  def rewrite[A: Plated](f: A => Option[A])(a: A): A =
    rewriteOf(plate[A].asSetter)(f)(a)

  /**
    * rewrite a target by applying a rule within a [[Setter]], as often as
    * possible until it reaches a fixpoint (this is an infinite loop if there is
    * no fixpoint)
    */
  def rewriteOf[A](l: Setter[A, A])(f: A => Option[A])(a: A): A = {
    def go(b: A): A = {
      val c = transformOf(l)(go)(b)
      f(c).fold(c)(go)
    }
    go(a)
  }

  /** transform every element */
  def transform[A: Plated](f: A => A)(a: A): A =
    transformOf(plate[A].asSetter)(f)(a)

  /** transform every element by applying a [[Setter]] */
  def transformOf[A](l: Setter[A, A])(f: A => A)(a: A): A =
    l.modify(b => transformOf(l)(f)(f(b)))(a)

}
