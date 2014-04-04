package monocle.std

import monocle._

object list extends ListInstances

trait ListInstances {
  def head[A] : SimpleLens[List[A], Option[A]] = {
    def _get(list: List[A]): Option[A] = list.headOption
    def _set(list: List[A], option: Option[A]): List[A] =
      if(list.isEmpty) option.toList else option.toList ::: list.tail
    SimpleLens[List[A], Option[A]](_get, _set)
  }


}