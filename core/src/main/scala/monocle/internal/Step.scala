package monocle.internal

import scalaz.Profunctor.UpStar
import scalaz.{ Applicative, Functor, Profunctor, Tag, -\/, \/, \/- }

trait Step[P[_, _]] extends ProChoice[P] with Strong[P] {
  def step[A, B, C, D](pab: P[A, B]): P[C \/ (A, D), C \/ (B, D)] =
    right(first(pab))
}

object Step {
  def apply[P[_, _]](implicit ev: Step[P]): Step[P] = ev

  implicit def function1Step: Step[Function1] = new Step[Function1] {
    @inline override def dimap[A, B, C, D](fab: A => B)(f: C => A)(g: B => D): C => D = g compose fab compose f
    @inline def mapfst[A, B, C](fab: A => B)(f: C => A) = fab compose f
    @inline def mapsnd[A, B, C](fab: A => B)(f: B => C) = f   compose fab

    @inline override def first[A, B, C](f: A => B) = ac => ac.copy(_1 = f(ac._1))
    @inline override def second[A, B, C](f: A => B) = ca => ca.copy(_2 = f(ca._2))

    @inline override def left[A, B, C](f: A => B) = _.leftMap(f)
    @inline override def right[A, B, C](f: A => B) = _.map(f)

    @inline override def step[A, B, C, D](f: A => B): C \/ (A, D) => C \/ (B, D) =
      _.map(ad => (f(ad._1), ad._2))
  }

  implicit def upStarStep[F[_]: Applicative]: Step[UpStar[F, ?, ?]] = new Step[UpStar[F, ?, ?]] {
    @inline override def dimap[A, B, C, D](pab: UpStar[F, A, B])(f: C => A)(g: B => D): UpStar[F, C, D] =
      UpStar(c => Functor[F].map(Tag.unwrap(pab)(f(c)))(g))
    @inline def mapfst[A, B, C](pab: UpStar[F, A, B])(f: C => A): UpStar[F, C, B] =
      UpStar(Tag.unwrap(pab) compose f)
    @inline def mapsnd[A, B, C](pab: UpStar[F, A, B])(f: B => C): UpStar[F, A, C] =
      UpStar(a => Functor[F].map(Tag.unwrap(pab)(a))(f))

    @inline override def first[A, B, C](pab: UpStar[F, A, B]): UpStar[F, (A, C), (B, C)] =
      UpStar[F, (A, C), (B, C)] {
        case (a, c) => Functor[F].strengthR(Tag.unwrap(pab)(a), c)
      }
    @inline override def second[A, B, C](pab: UpStar[F, A, B]): UpStar[F, (C, A), (C, B)] =
      UpStar[F, (C, A), (C, B)] {
        case (c, a) => Functor[F].strengthL(c, Tag.unwrap(pab)(a))
      }

    @inline override def left[A, B, C](pab: UpStar[F, A, B]): UpStar[F, A \/ C, B \/ C] =
      UpStar(_.fold(a => Applicative[F].map(Tag.unwrap(pab)(a))(\/.left), c => Applicative[F].point(\/-(c))))

    @inline override def right[A, B, C](pab: UpStar[F, A, B]): UpStar[F, C \/ A, C \/ B] =
      UpStar(_.fold(c => Applicative[F].point(-\/(c)), a => Applicative[F].map(Tag.unwrap(pab)(a))(\/.right)))

    @inline override def step[A, B, C, D](pab: UpStar[F, A, B]): UpStar[F, C \/ (A, D), C \/ (B, D)] =
      UpStar(_.fold(c => Applicative[F].point(-\/(c)), { case (a, d) => Functor[F].map(Tag.unwrap(pab)(a))(a2 => \/-((a2, d))) }))
  }

}
