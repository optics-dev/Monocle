package monocle

import scalaz.Monoid


trait Fold[S, A] {

  def fold(from: S)(implicit ev: Monoid[A]): A

}
