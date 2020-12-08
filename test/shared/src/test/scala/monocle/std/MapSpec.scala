package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.function._

import scala.annotation.nowarn

class MapSpec extends MonocleSuite {
  checkAll("at Map", AtTests[Map[Int, String], Int, Option[String]])
  checkAll("empty Map", EmptyTests[Map[Int, String]]): @nowarn
  checkAll("index Map", IndexTests[Map[Int, String], Int, String])
}
