package monocle.std

import monocle.{Traversal, Lens}

object tuple extends TupleInstances

trait TupleInstances {

  def _1[A, B, New]: Lens[(A, B), (New,B), A, New] = Lens(_._1, (pair, n) => pair.copy(_1 = n))
  def _2[A, B, New]: Lens[(A, B), (A,New), B, New] = Lens(_._2, (pair, n) => pair.copy(_2 = n))

  def both[A, B]: Traversal[(A, A), (B, B), A, B]  =
    Traversal.make2[(A, A), (B, B), A, B](_._1)(_._2)((_, b1, b2) => (b1, b2))

}
