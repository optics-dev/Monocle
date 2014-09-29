package monocle.internal

import scalaz.Profunctor.UpStar
import scalaz.std.function._
import scalaz.{Applicative, Functor, Profunctor, -\/, \/, \/-, Tag}

trait Walk[P[_,_]] extends Step[P] {
  def pureP[A]: P[A, A]
  def apP[A, B, C](pab: P[A, B])(f: P[A, B => C]): P[A, C]
}

object Walk {
  def apply[P[_, _]](implicit ev: Walk[P]): Walk[P] = ev

  implicit val function1Walk = new Walk[Function1] {
    def pureP[A]: A => A = identity
    def apP[A, B, C](pab: A => B)(f: A => B => C): A => C = a => f(a)(pab(a))

    def mapfst[A, B, C](pab: A => B)(f: C => A) = Profunctor[Function1].mapfst(pab)(f)
    def mapsnd[A, B, C](pab: A => B)(f: B => C) = Profunctor[Function1].mapsnd(pab)(f)

    override def first[A, B, C](f: A => B) = ac => ac.copy(_1 = f(ac._1))
    override def second[A, B, C](f: A => B)= ca => ca.copy(_2 = f(ca._2))

    override def left[A, B, C](f: A => B)  = _.leftMap(f)
    override def right[A, B, C](f: A => B)= _.map(f)
  }

  implicit def upStarWalk[F[_]: Applicative]: Walk[UpStar[F, ?, ?]] = new Walk[UpStar[F, ?, ?]] {
    def pureP[A]: UpStar[F, A, A] = UpStar(Applicative[F].point(_))
    def apP[A, B, C](pab: UpStar[F, A, B])(f: UpStar[F, A, B => C]): UpStar[F, A, C] =
      UpStar(a =>
        Applicative[F].ap(Tag.unwrap(pab)(a))(Tag.unwrap(f)(a))
      )

    def mapfst[A, B, C](pab: UpStar[F, A, B])(f: C => A): UpStar[F, C, B] =
      UpStar(Tag.unwrap(pab) compose f)
    def mapsnd[A, B, C](pab: UpStar[F, A, B])(f: B => C): UpStar[F, A, C] =
      UpStar(a => Functor[F].map(Tag.unwrap(pab)(a))(f))

    override def first[A, B, C](pab: UpStar[F, A, B]): UpStar[F, (A, C), (B, C)] =
      UpStar[F, (A, C), (B, C)]{
        case (a, c) => Functor[F].strengthR(Tag.unwrap(pab)(a), c)
      }
    override def second[A, B, C](pab: UpStar[F, A, B]): UpStar[F, (C, A), (C, B)] =
      UpStar[F, (C, A), (C, B)]{
        case (c, a) => Functor[F].strengthL(c, Tag.unwrap(pab)(a))
      }

    override def left[A, B, C](pab: UpStar[F, A, B]): UpStar[F, A \/ C, B \/ C] =
      UpStar[F, A \/ C, B \/ C](_.fold(a => Applicative[F].map(Tag.unwrap(pab)(a))(\/.left), c => Applicative[F].point(\/-(c))))

    override def right[A, B, C](pab: UpStar[F, A, B]): UpStar[F, C \/ A, C \/ B] =
      UpStar[F, C \/ A, C \/ B](_.fold(c => Applicative[F].point(-\/(c)), a => Applicative[F].map(Tag.unwrap(pab)(a))(\/.right)))
  }


}
