package monocle.function

import monocle.std._
import monocle.syntax._
import org.specs2.scalaz.Spec

import scalaz.{Maybe, IList}


class TailMaybeExample extends Spec {

  "tailMaybe creates an Optional from a List, IList, Vector or Stream to its tail" in {
    (List(1, 2, 3)    applyOptional tailMaybe getMaybe) ==== Maybe.just(List(2, 3))
    (List(1)          applyOptional tailMaybe getMaybe) ==== Maybe.just(Nil)
    ((Nil: List[Int]) applyOptional tailMaybe getMaybe) ==== Maybe.empty

    (List(1, 2, 3)    applyOptional tailMaybe set List(4, 5, 6))   ==== List(1, 4, 5, 6)
    (IList(1, 2, 3)   applyOptional tailMaybe set IList(4, 5, 6))  ==== IList(1, 4, 5, 6)
    (Vector(1, 2, 3)  applyOptional tailMaybe set Vector(4, 5, 6)) ==== Vector(1, 4, 5, 6)
    (Stream(1, 2, 3)  applyOptional tailMaybe set Stream(4, 5, 6)) ==== Stream(1, 4, 5, 6)
  }


  "tailMaybe creates an Optional from a String to its tail" in {
    ("hello" applyOptional tailMaybe modify (_.toUpperCase)) ==== "hELLO"
  }

}
