package monocle.internal

import scalaz.Profunctor.UpStar
import scalaz.{Applicative, Profunctor, \/}


trait ProChoice[P[_, _]] extends Profunctor[P] {
  def left[A, B, C](pab: P[A, B]): P[A \/ C, B \/ C] =
    dimap(right[A, B, C](pab))((_: A \/ C).swap)(_.swap)

  def right[A, B, C](pab: P[A, B]): P[C \/ A, C \/ B] =
    dimap(left[A, B, C](pab))((_: C \/ A).swap)(_.swap)
}


object ProChoice {

  def apply[P[_, _]](implicit ev: ProChoice[P]): ProChoice[P] = ev

  implicit val function1ProChoice: ProChoice[Function1] = Step[Function1]
  implicit def upStarProChoice[F[_]: Applicative]: ProChoice[UpStar[F, ?, ?]] = Step.upStarStep[F]

}