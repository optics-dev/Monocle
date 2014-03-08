package monocle.std

import monocle.Lens

object tuple extends TupleInstances

trait TupleInstances {

  def _1[A, B, New]: Lens[(A, B), (New,B), A, New] = Lens(_._1, (pair, n) => pair.copy(_1 = n))
  def _2[A, B, New]: Lens[(A, B), (A,New), B, New] = Lens(_._2, (pair, n) => pair.copy(_2 = n))

}
