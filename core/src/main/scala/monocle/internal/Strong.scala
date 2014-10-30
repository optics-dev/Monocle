package monocle.internal

import scalaz.Profunctor.UpStar
import scalaz.{Functor, Profunctor, Tag}

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

  implicit val function1Strong: Strong[Function1] = Step[Function1]

  implicit def upStarStrong[F[_]: Functor]: Strong[UpStar[F, ?, ?]] = new Strong[UpStar[F, ?, ?]]{
    override def first[A, B, C](pab: UpStar[F, A, B]): UpStar[F, (A, C), (B, C)] =
      UpStar[F, (A, C), (B, C)]{
        case (a, c) => Functor[F].strengthR(Tag.unwrap(pab)(a), c)
      }
    override def second[A, B, C](pab: UpStar[F, A, B]): UpStar[F, (C, A), (C, B)] =
      UpStar[F, (C, A), (C, B)]{
        case (c, a) => Functor[F].strengthL(c, Tag.unwrap(pab)(a))
      }

    def mapfst[A, B, C](pab: UpStar[F, A, B])(f: C => A): UpStar[F, C, B] =
      UpStar(Tag.unwrap(pab) compose f)
    def mapsnd[A, B, C](pab: UpStar[F, A, B])(f: B => C): UpStar[F, A, C] =
      UpStar(a => Functor[F].map(Tag.unwrap(pab)(a))(f))
  }

}
