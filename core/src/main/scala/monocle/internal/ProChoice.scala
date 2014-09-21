package monocle.internal

import scalaz.{Profunctor, \/, -\/, \/-, Applicative, Kleisli}
import scalaz.std.function._


trait ProChoice[P[_, _]] extends Profunctor[P] {
  def left[A, B, C](pab: P[A, B]): P[A \/ C, B \/ C]
  def right[A, B, C](pab: P[A, B]): P[C \/ A, C \/ B]
}


object ProChoice {

  def apply[P[_, _]](implicit ev: ProChoice[P]): ProChoice[P] = ev

  implicit val function1ProChoice = new ProChoice[Function1] {
    def left[A, B, C] (pab: A => B): A \/ C => B \/ C = _.leftMap(pab)
    def right[A, B, C](pab: A => B): C \/ A => C \/ B = _.map(pab)

    def mapfst[A, B, C](fab: A => B)(f: C => A): C => B = Profunctor[Function1].mapfst(fab)(f)
    def mapsnd[A, B, C](fab: A => B)(f: B => C): A => C = Profunctor[Function1].mapsnd(fab)(f)
  }

  implicit def kleisliProChoice[F[_]: Applicative]: ProChoice[({type λ[α,β] = Kleisli[F,α,β]})#λ] = new ProChoice[({type λ[α,β] = Kleisli[F,α,β]})#λ] {
    def mapfst[A, B, C](pab: Kleisli[F, A, B])(f: C => A): Kleisli[F, C, B] =
      Profunctor[({type λ[α,β] = Kleisli[F,α,β]})#λ].mapfst(pab)(f)
    def mapsnd[A, B, C](pab: Kleisli[F, A, B])(f: B => C): Kleisli[F, A, C] =
      Profunctor[({type λ[α,β] = Kleisli[F,α,β]})#λ].mapsnd(pab)(f)

    def left[A, B, C](pab: Kleisli[F, A, B]): Kleisli[F, A \/ C, B \/ C] =
      Kleisli[F, A \/ C, B \/ C](_.fold(a => Applicative[F].map(pab.run(a))(\/.left), c => Applicative[F].point(\/-(c))))
    def right[A, B, C](pab: Kleisli[F, A, B]): Kleisli[F, C \/ A, C \/ B] =
      Kleisli[F, C \/ A, C \/ B](_.fold(c => Applicative[F].point(-\/(c)), a => Applicative[F].map(pab.run(a))(\/.right)))
  }

}