package monocle.internal

import scalaz.{Applicative, Kleisli, \/}

trait Step[P[_, _]] extends ProChoice[P] with Strong[P] {
  def step[A, B, C, D](pab: P[A, B]): P[C \/ (A, D), C \/ (B, D)] =
    right(first(pab))
}

object Step {
  def apply[P[_, _]](implicit ev: Step[P]): Step[P] = ev

  implicit def function1Step: Step[Function1] = Walk[Function1]
  implicit def kleisliStep[F[_]: Applicative]: Step[Kleisli[F, ?, ?]] = Walk[Kleisli[F, ?, ?]]

}
