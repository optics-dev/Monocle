package monocle.std

import monocle._

object list extends ListInstances

trait ListInstances {
  def head[A] : SimpleLens[List[A], Option[A]] = {
    def _get(list: List[A]): Option[A] = list.headOption
    def _set(list: List[A], option: Option[A]): List[A] = (list,option) match {
      case(Nil,   None   ) => Nil
      case(Nil,   Some(a)) => a :: Nil
      case(x::xs, None   ) => xs
      case(x::xs, Some(a)) => a :: xs
    }
    SimpleLens[List[A], Option[A]](_get, _set)
  }


}