package monocle.internal

import scalaz.{Applicative, Profunctor}

case class Ap[P[_, _], A, B](pab: P[A, B])

object Ap {

  implicit def apApplicative[C, P[_, _]: Walk]: Applicative[({type λ[α] = Ap[P, C, α]})#λ] = new Applicative[({type λ[α] = Ap[P, C, α]})#λ]{
    def point[A](a: => A): Ap[P, C, A] = Ap(Profunctor[P].mapsnd(Walk[P].pureP[C])(_ => a))
    def ap[A, B](fa: => Ap[P, C, A])(f: => Ap[P, C, A => B]): Ap[P, C, B] = Ap(Walk[P].apP(fa.pab)(f.pab))
  }
}