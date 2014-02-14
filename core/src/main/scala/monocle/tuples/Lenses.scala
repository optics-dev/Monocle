package monocle.tuples

import monocle.SimpleLens


object Lenses {

  def _1[S, A](implicit ev: First[S, A]):  SimpleLens[S, A] = SimpleLens[S, A](ev.get_1, ev.set_1)
  def _2[S, A](implicit ev: Second[S, A]): SimpleLens[S, A] = SimpleLens[S, A](ev.get_2, ev.set_2)

}
