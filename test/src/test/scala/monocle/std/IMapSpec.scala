package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.function._

import scalaz.==>>

class IMapSpec extends MonocleSuite {
  checkAll("at ==>>", AtTests.defaultIntIndex[Int ==>> String, Option[String]])
  checkAll("each ==>>", EachTests[Int ==>> String, String])
  checkAll("empty ==>>", EmptyTests[Int ==>> String])
  checkAll("filterIndex ==>>", FilterIndexTests.evenIndex[Int ==>> Char, Char])
  checkAll("index ==>>", IndexTests.defaultIntIndex[Int ==>> String, String])
}
