package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.function._

import scalaz.==>>

class IMapSpec extends MonocleSuite {
  checkAll("at ==>>", AtTests[Int ==>> String, Int, Option[String]])
  checkAll("each ==>>", EachTests[Int ==>> String, String])
  checkAll("empty ==>>", EmptyTests[Int ==>> String])
  checkAll("filterIndex ==>>", FilterIndexTests[Int ==>> Char, Int, Char])
  checkAll("index ==>>", IndexTests[Int ==>> String, Int, String])
}
