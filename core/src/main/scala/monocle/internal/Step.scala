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
    override def dimap[A, B, C, D](fab: A => B)(f: C => A)(g: B => D): C => D = g compose fab compose f
    def mapfst[A, B, C](pab: A => B)(f: C => A) = Profunctor[Function1].mapfst(pab)(f)
    def mapsnd[A, B, C](pab: A => B)(f: B => C) = Profunctor[Function1].mapsnd(pab)(f)

    override def first[A, B, C](f: A => B) = ac => ac.copy(_1 = f(ac._1))
    override def second[A, B, C](f: A => B) = ca => ca.copy(_2 = f(ca._2))

    override def left[A, B, C](f: A => B) = _.leftMap(f)
    override def right[A, B, C](f: A => B) = _.map(f)
  }

  implicit def upStarStep[F[_]: Applicative]: Step[UpStar[F, ?, ?]] = new Step[UpStar[F, ?, ?]] {
    override def dimap[A, B, C, D](pab: UpStar[F, A, B])(f: C => A)(g: B => D): UpStar[F, C, D] =
      UpStar(c => Functor[F].map(Tag.unwrap(pab)(f(c)))(g))
    def mapfst[A, B, C](pab: UpStar[F, A, B])(f: C => A): UpStar[F, C, B] =
      UpStar(Tag.unwrap(pab) compose f)
    def mapsnd[A, B, C](pab: UpStar[F, A, B])(f: B => C): UpStar[F, A, C] =
      UpStar(a => Functor[F].map(Tag.unwrap(pab)(a))(f))

    override def first[A, B, C](pab: UpStar[F, A, B]): UpStar[F, (A, C), (B, C)] =
      UpStar[F, (A, C), (B, C)] {
        case (a, c) => Functor[F].strengthR(Tag.unwrap(pab)(a), c)
      }
    override def second[A, B, C](pab: UpStar[F, A, B]): UpStar[F, (C, A), (C, B)] =
      UpStar[F, (C, A), (C, B)] {
        case (c, a) => Functor[F].strengthL(c, Tag.unwrap(pab)(a))
      }

    override def left[A, B, C](pab: UpStar[F, A, B]): UpStar[F, A \/ C, B \/ C] =
      UpStar[F, A \/ C, B \/ C](_.fold(a => Applicative[F].map(Tag.unwrap(pab)(a))(\/.left), c => Applicative[F].point(\/-(c))))

    override def right[A, B, C](pab: UpStar[F, A, B]): UpStar[F, C \/ A, C \/ B] =
      UpStar[F, C \/ A, C \/ B](_.fold(c => Applicative[F].point(-\/(c)), a => Applicative[F].map(Tag.unwrap(pab)(a))(\/.right)))
  }

}
