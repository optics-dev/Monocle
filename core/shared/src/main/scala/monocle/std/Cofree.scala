package monocle.std

import monocle.{Iso, PIso}

import scalaz.Tree.Node
import scalaz.{Cofree, EphemeralStream, OneAnd, Tree}
import EphemeralStream.##::

object cofree extends CofreeOptics

trait CofreeOptics {

  /** Polymorphic isomorphism between `Cofree[Option, _]` and `OneAnd[EphemeralStream, _]` */
  def pCofreeToStream[A, B]: PIso[Cofree[Option, A], Cofree[Option, B],
                                  OneAnd[EphemeralStream, A], OneAnd[EphemeralStream, B]] =
    PIso[Cofree[Option, A], Cofree[Option, B], OneAnd[EphemeralStream, A], OneAnd[EphemeralStream, B]](
      (c: Cofree[Option, A]) => OneAnd[EphemeralStream, A](c.head, toStream(c.tail))
    ){ case OneAnd(head, tail) => fromStream(head, tail) }

  /** [[Iso]] variant of [[pCofreeToStream]]  */
  def cofreeToStream[A]: Iso[Cofree[Option, A], OneAnd[EphemeralStream, A]] =
    pCofreeToStream[A, A]


  private def toTree[A](c: Cofree[EphemeralStream, A]): Tree[A] =
    Node(c.head, c.tail.map(toTree[A]))

  private def fromTree[A](c: Tree[A]): Cofree[EphemeralStream, A] =
    Cofree.delay(c.rootLabel, c.subForest.map(fromTree[A]))

  /** Polymorphic isomorphism between `Cofree[Stream, _]` and `Tree` */
  def pCofreeToTree[A, B]: PIso[Cofree[EphemeralStream, A], Cofree[EphemeralStream, B],
                                Tree[A], Tree[B]] =
    PIso[Cofree[EphemeralStream, A], Cofree[EphemeralStream, B], Tree[A], Tree[B]](toTree[A])(fromTree[B])

  /** [[Iso]] variant of [[pCofreeToTree]] */
  def cofreeToTree[A]: Iso[Cofree[EphemeralStream, A], Tree[A]] =
    pCofreeToTree[A, A]

  private def toStream[A](optC: Option[Cofree[Option, A]]): EphemeralStream[A] =
    optC.fold(EphemeralStream.apply[A])(c => EphemeralStream.cons(c.head, toStream(c.tail)))

  private def fromStream[A, B](z: A, c: EphemeralStream[A]): Cofree[Option, A] = c match {
    case head ##:: tail => Cofree.delay(z, Some(fromStream(head, tail)))
    case _ => Cofree(z, None: Option[Cofree[Option, A]])
  }
}
