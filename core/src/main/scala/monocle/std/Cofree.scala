package monocle.std

import monocle.function._
import monocle.{Lens, PIso, Iso, Optional, PTraversal, Traversal}

import scalaz.Cofree._
import scalaz.{Cofree, Free, Applicative, Traverse, OneAnd, Tree}

object cofree extends CofreeOptics

trait CofreeOptics {

  private def toStream[A](c: Cofree[Option, A]): Stream[A] =
    c.head #:: c.tail.fold(Stream.empty[A])(toStream[A])

  private def fromStream[A, B](z: A, c: Stream[A]): Cofree[Option, A] = c match {
    case head #:: tail => Cofree.delay(z, Some(fromStream(head, tail)))
    case _ => Cofree(z, None: Option[Cofree[Option, A]])
  }

  /** Polymorphic isomorphism between `Cofree[Option, _]` and `OneAnd[Stream, _]` */
  def pCofreeToStream[A, B]: PIso[Cofree[Option, A], Cofree[Option, B],
                                  OneAnd[Stream, A], OneAnd[Stream, B]] =
    PIso[Cofree[Option, A], Cofree[Option, B], OneAnd[Stream, A], OneAnd[Stream, B]](
      (c: Cofree[Option, A]) => OneAnd[Stream, A](c.head, toStream(c))
    ){ case OneAnd(head, tail) => fromStream(head, tail) }

  /** [[Iso]] variant of [[pCofreeToStream]]  */
  def cofreeToStream[A]: Iso[Cofree[Option, A], OneAnd[Stream, A]] =
    pCofreeToStream[A, A]


  private def toTree[A](c: Cofree[Stream, A]): Tree[A] =
    Tree.node(c.head, c.tail.map(toTree[A]))

  private def fromTree[A](c: Tree[A]): Cofree[Stream, A] =
    Cofree.delay(c.rootLabel, c.subForest.map(fromTree[A]))

  /** Polymorphic isomorphism between `Cofree[Stream, _]` and `Tree` */
  def pCofreeToTree[A, B]: PIso[Cofree[Stream, A], Cofree[Stream, B],
                                Tree[A], Tree[B]] =
    PIso[Cofree[Stream, A], Cofree[Stream, B], Tree[A], Tree[B]](toTree[A])(fromTree[B])

  /** [[Iso]] variant of [[pCofreeToTree]] */
  def cofreeToTree[A]: Iso[Cofree[Stream, A], Tree[A]] =
    pCofreeToTree[A, A]


  /** Evidence that cofree structures can be split up into a `head` and a `tail` */
  implicit def cofreeCons1[S[_], A]: Cons1[Cofree[S, A], A, S[Cofree[S, A]]] =
    new Cons1[Cofree[S, A], A, S[Cofree[S, A]]] {

      def cons1: Iso[Cofree[S, A], (A, S[Cofree[S, A]])]  =
        Iso((c: Cofree[S, A]) => (c.head, c.tail)){ case (h, t) => Cofree(h, t) }

      /** Overridden to prevent forcing evaluation of the `tail` when we're only
        * interested in using the `head` */
      override def head: Lens[Cofree[S, A], A] =
        Lens((c: Cofree[S, A]) => c.head)(h => c => Cofree.delay(h, c.tail))
    }


  /** Trivial [[Each]] instance due to `Cofree S` being traversable when `S` is */
  implicit def cofreeEach[S[_] : Traverse, A]: Each[Cofree[S, A], A] =
    Each.traverseEach[({type L[X] = Cofree[S, X]})#L, A]
}
