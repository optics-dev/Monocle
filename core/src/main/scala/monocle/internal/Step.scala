package monocle.internal

import scalaz.{Profunctor, Kleisli, \/}

trait Step[P[_, _]] extends ProChoice[P] with Strong[P] {
  def step[A, B, C, D](pab: P[A, B]): P[C \/ (A, D), C \/ (B, D)] =
    right(first(pab))
}

object Step {
  def apply[P[_, _]](implicit ev: Step[P]): Step[P] = ev

  implicit def function1Step: Step[Function1] = new Step[Function1] {
    def mapfst[A, B, C](pab: A => B)(f: C => A) = Profunctor[Function1].mapfst(pab)(f)
    def mapsnd[A, B, C](pab: A => B)(f: B => C) = Profunctor[Function1].mapsnd(pab)(f)

    def first[A, B, C](pab: A => B)  = Strong[Function1].first(pab)
    def second[A, B, C](pab: A => B) = Strong[Function1].second(pab)

    def left[A, B, C](pab: A => B)  = ProChoice[Function1].left(pab)
    def right[A, B, C](pab: A => B) = ProChoice[Function1].right(pab)
  }

  implicit def kleisliStep[F[_]]: Step[({type λ[α,β] = Kleisli[F,α,β]})#λ] = new Step[({type λ[α,β] = Kleisli[F,α,β]})#λ] {
    def mapfst[A, B, C](pab: Kleisli[F, A, B])(f: C => A): Kleisli[F, C, B] =
      Profunctor[({type λ[α,β] = Kleisli[F,α,β]})#λ].mapfst(pab)(f)
    def mapsnd[A, B, C](pab: Kleisli[F, A, B])(f: B => C): Kleisli[F, A, C] =
      Profunctor[({type λ[α,β] = Kleisli[F,α,β]})#λ].mapsnd(pab)(f)

    def first[A, B, C](pab: Kleisli[F, A, B]): Kleisli[F, (A, C), (B, C)] =
      Strong[({type λ[α,β] = Kleisli[F,α,β]})#λ].first(pab)
    def second[A, B, C](pab: Kleisli[F, A, B]): Kleisli[F, (C, A), (C, B)] =
      Strong[({type λ[α,β] = Kleisli[F,α,β]})#λ].second(pab)

    def left[A, B, C](pab: Kleisli[F, A, B]): Kleisli[F, A \/ C, B \/ C] =
      ProChoice[({type λ[α,β] = Kleisli[F,α,β]})#λ].left(pab)
    def right[A, B, C](pab: Kleisli[F, A, B]): Kleisli[F, C \/ A, C \/ B] =
      ProChoice[({type λ[α,β] = Kleisli[F,α,β]})#λ].right(pab)
  }

}
