package monocle.std

import monocle._

object list extends ListInstances

trait ListInstances {

  def last[A] : SimpleLens[List[A], Option[A]] = {
    def _get(list: List[A]): Option[A] = list.lastOption
    def _set(list: List[A], option: Option[A]): List[A] = (list, option) match {
      case(Nil,   None  ) => Nil
      case(Nil,   Some(a)) => a :: Nil
      case(xs,    None  ) => xs.init
      case(xs,    Some(a)) => xs.init ++ List(a)
    }

    SimpleLens[List[A], Option[A]](_get, _set)
  }


}