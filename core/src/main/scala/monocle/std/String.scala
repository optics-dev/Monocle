package monocle.std

import monocle.SimpleIso

object string extends StringInstances

trait StringInstances {

  val stringToList = SimpleIso[String, List[Char]](_.toList, _.mkString(""))

}
