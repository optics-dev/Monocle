package monocle.internal

import scalaz.{Profunctor, \/}
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

}