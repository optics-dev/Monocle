package monocle.std

import monocle.SimpleLens

object tuple extends TupleInstances

trait TupleInstances {

  def _1[A, B]: SimpleLens[(A, B), A] = SimpleLens(_._1, (pair, s) => pair.copy(_1 = s))
  def _2[A, B]: SimpleLens[(A, B), B] = SimpleLens(_._2, (pair, t) => pair.copy(_2 = t))

}
