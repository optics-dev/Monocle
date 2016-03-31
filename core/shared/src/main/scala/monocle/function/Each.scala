package monocle.function

import monocle.{Iso, PTraversal, Traversal}

import scala.annotation.implicitNotFound
import scalaz.Traverse

/**
 * Typeclass that defines a [[Traversal]] from a monomorphic container `S` to all of its elements of type `A`
 * @tparam S source of [[Traversal]]
 * @tparam A target of [[Traversal]], `A` is supposed to be unique for a given `S`
 */
@implicitNotFound("Could not find an instance of Each[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Each[S, A] extends Serializable {
  def each: Traversal[S, A]
}

object Each extends EachFunctions {
  /** lift an instance of [[Each]] using an [[Iso]] */
  def fromIso[S, A, B](iso: Iso[S, A])(implicit ev: Each[A, B]): Each[S, B] = new Each[S, B] {
    override def each: Traversal[S, B] =
      iso composeTraversal ev.each
  }
}


trait EachFunctions {
  def each[S, A](implicit ev: Each[S, A]): Traversal[S, A] = ev.each

  def traverseEach[S[_]: Traverse, A]: Each[S[A], A] = new Each[S[A], A] {
    def each = PTraversal.fromTraverse[S, A, A]
  }
}