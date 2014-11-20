package monocle.internal

import scalaz.Profunctor.UpStar
import scalaz.syntax.tag._
import scalaz.{-\/, Applicative, Functor, Profunctor, \/, \/-}


trait ProChoice[P[_, _]] extends Profunctor[P] {
  def left[A, B, C](pab: P[A, B]): P[A \/ C, B \/ C] =
    dimap(right[A, B, C](pab))((_: A \/ C).swap)(_.swap)

  def right[A, B, C](pab: P[A, B]): P[C \/ A, C \/ B] =
    dimap(left[A, B, C](pab))((_: C \/ A).swap)(_.swap)
}


object ProChoice {

  def apply[P[_, _]](implicit ev: ProChoice[P]): ProChoice[P] = ev

  implicit val function1ProChoice: ProChoice[Function1] = new ProChoice[Function1] {
    @inline override def dimap[A, B, C, D](fab: A => B)(f: C => A)(g: B => D): C => D = g compose fab compose f
    @inline def mapfst[A, B, C](fab: A => B)(f: C => A) = fab compose f
    @inline def mapsnd[A, B, C](fab: A => B)(f: B => C) = f   compose fab

    @inline override def left[A, B, C](f: A => B) = _.leftMap(f)
    @inline override def right[A, B, C](f: A => B) = _.map(f)
  }

  implicit def upStarProChoice[F[_]: Applicative]: ProChoice[UpStar[F, ?, ?]] = new ProChoice[UpStar[F, ?, ?]] {
    @inline override def dimap[A, B, C, D](pab: UpStar[F, A, B])(f: C => A)(g: B => D): UpStar[F, C, D] =
      UpStar(c => Functor[F].map(pab.unwrap(f(c)))(g))
    @inline def mapfst[A, B, C](pab: UpStar[F, A, B])(f: C => A): UpStar[F, C, B] =
      UpStar(pab.unwrap compose f)
    @inline def mapsnd[A, B, C](pab: UpStar[F, A, B])(f: B => C): UpStar[F, A, C] =
      UpStar(a => Functor[F].map(pab.unwrap(a))(f))

    @inline override def left[A, B, C](pab: UpStar[F, A, B]): UpStar[F, A \/ C, B \/ C] =
      UpStar(_.fold(a => Applicative[F].map(pab.unwrap(a))(\/.left), c => Applicative[F].point(\/-(c))))

    @inline override def right[A, B, C](pab: UpStar[F, A, B]): UpStar[F, C \/ A, C \/ B] =
      UpStar(_.fold(c => Applicative[F].point(-\/(c)), a => Applicative[F].map(pab.unwrap(a))(\/.right)))
  }

}