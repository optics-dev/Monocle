package monocle.internal

import scalaz.Profunctor.UpStar
import scalaz.syntax.tag._
import scalaz.{Functor, Profunctor}

/**
 * Generalizing upstar of a strong Functor
 */
trait Strong[P[_, _]] extends Profunctor[P] {
  def first[A, B, C] (pab: P[A, B]): P[(A, C), (B, C)] =
    dimap(second[A, B, C](pab))((_: (A, C)).swap)(_.swap)
  def second[A, B, C](pab: P[A, B]): P[(C, A), (C, B)] =
    dimap(first[A, B, C](pab))((_: (C, A)).swap)(_.swap)
}

object Strong {

  def apply[P[_, _]](implicit ev: Strong[P]): Strong[P] = ev

  implicit val function1Strong: Strong[Function1] = new Strong[Function1] {
    @inline override def dimap[A, B, C, D](fab: A => B)(f: C => A)(g: B => D): C => D = g compose fab compose f
    @inline def mapfst[A, B, C](fab: A => B)(f: C => A) = fab compose f
    @inline def mapsnd[A, B, C](fab: A => B)(f: B => C) = f   compose fab

    @inline override def first[A, B, C](f: A => B) = ac => ac.copy(_1 = f(ac._1))
    @inline override def second[A, B, C](f: A => B) = ca => ca.copy(_2 = f(ca._2))
  }

  implicit def upStarStrong[F[_]: Functor]: Strong[UpStar[F, ?, ?]] = new Strong[UpStar[F, ?, ?]]{
    @inline override def dimap[A, B, C, D](pab: UpStar[F, A, B])(f: C => A)(g: B => D): UpStar[F, C, D] =
      UpStar(c => Functor[F].map(pab.unwrap(f(c)))(g))
    @inline def mapfst[A, B, C](pab: UpStar[F, A, B])(f: C => A): UpStar[F, C, B] =
      UpStar(pab.unwrap compose f)
    @inline def mapsnd[A, B, C](pab: UpStar[F, A, B])(f: B => C): UpStar[F, A, C] =
      UpStar(a => Functor[F].map(pab.unwrap(a))(f))
    @inline override def first[A, B, C](pab: UpStar[F, A, B]): UpStar[F, (A, C), (B, C)] =
      UpStar[F, (A, C), (B, C)]{
        case (a, c) => Functor[F].strengthR(pab.unwrap(a), c)
      }
    @inline override def second[A, B, C](pab: UpStar[F, A, B]): UpStar[F, (C, A), (C, B)] =
      UpStar[F, (C, A), (C, B)]{
        case (c, a) => Functor[F].strengthL(c, pab.unwrap(a))
      }
  }

}
