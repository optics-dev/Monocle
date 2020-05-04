package monocle.std

import monocle.{Iso, PIso}

import cats.{Later, Now}
import cats.data.OneAnd
import cats.free.Cofree

object cofree extends CofreeOptics

trait CofreeOptics {

  /** Polymorphic isomorphism between `Cofree[Option, _]` and `OneAnd[Stream, _]` */
  def pCofreeToStream[A, B]: PIso[Cofree[Option, A], Cofree[Option, B], OneAnd[Stream, A], OneAnd[Stream, B]] =
    PIso[Cofree[Option, A], Cofree[Option, B], OneAnd[Stream, A], OneAnd[Stream, B]]((c: Cofree[Option, A]) =>
      OneAnd[Stream, A](c.head, toStream(c.tail.value))
    ) { case OneAnd(head, tail) => fromStream(head, tail) }

  /** [[Iso]] variant of [[pCofreeToStream]]  */
  def cofreeToStream[A]: Iso[Cofree[Option, A], OneAnd[Stream, A]] =
    pCofreeToStream[A, A]

  private def toStream[A](optC: Option[Cofree[Option, A]]): Stream[A] =
    optC.fold(Stream.empty[A])(c => c.head #:: toStream(c.tail.value))

  private def fromStream[A, B](z: A, c: Stream[A]): Cofree[Option, A] =
    c match {
      case head #:: tail => Cofree(z, Later(Some(fromStream(head, tail))))
      case _             => Cofree(z, Now(None: Option[Cofree[Option, A]]))
    }
}
