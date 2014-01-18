package lens

import scalaz.Monoid


trait Traversal[A, B] {

  def get(from: A)(implicit ev: Monoid[B]): B

  def set(from: A, newValue: B): A = modify(from, _ => newValue)

  def modify(from: A, f: B => B): A

}
