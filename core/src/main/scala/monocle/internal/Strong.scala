package monocle.internal

import scalaz.{Functor, Kleisli, Profunctor}

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

  implicit val function1Strong: Strong[Function1] = Walk[Function1]

  implicit def kleisliStrong[F[_]: Functor] = new Strong[Kleisli[F, ?, ?]]{
    override def first[A, B, C](f: Kleisli[F, A, B]): Kleisli[F, (A, C), (B, C)] =
      Kleisli[F, (A, C), (B, C)] {
        case (a, c) => Functor[F].strengthR(f.run(a), c)
      }

    def mapfst[A, B, C](fab: Kleisli[F, A, B])(f: C => A): Kleisli[F, C, B] =
      Profunctor[Kleisli[F, ?, ?]].mapfst(fab)(f)
    def mapsnd[A, B, C](fab: Kleisli[F, A, B])(f: B => C): Kleisli[F, A, C] =
      Profunctor[Kleisli[F, ?, ?]].mapsnd(fab)(f)
  }

}
