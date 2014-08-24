package monocle.internal

import scalaz.{Profunctor, \/}


private[monocle] trait ProChoice[P[_, _]] extends Profunctor[P] {
  def left[A, B, C](pab: P[A, B]): P[A \/ C, B \/ C]
  def right[A, B, C](pab: P[A, B]): P[C \/ A, C \/ B]
}


object ProChoice {

  def apply[P[_, _]](implicit ev: ProChoice[P]): ProChoice[P] = ev

  implicit val function1ProChoice = new ProChoice[Function1] {
    def left[A, B, C] (pab: A => B): A \/ C => B \/ C = _.leftMap(pab)
    def right[A, B, C](pab: A => B): C \/ A => C \/ B = _.map(pab)

    def mapfst[A, B, C](fab: A => B)(f: C => A): C => B = fab compose f
    def mapsnd[A, B, C](fab: A => B)(f: B => C): A => C = f   compose fab
  }

}