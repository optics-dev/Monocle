package monocle.std

import monocle._

object list extends ListInstances

trait ListInstances {
  def head[A] : SimpleLens[List[A], Option[A]] = ???
}