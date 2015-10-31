package monocle.example

import monocle.Iso

object IsoExample {

  def listToVector[A] = Iso[List[A], Vector[A]](_.toVector)(_.toList)

  val stringToList = Iso[String, List[Char]](_.toList)(_.mkString(""))

}
