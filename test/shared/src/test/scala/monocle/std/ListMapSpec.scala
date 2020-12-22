package monocle.std

import cats.kernel.Eq
import monocle.MonocleSuite
import monocle.law.discipline.function._

import scala.collection.immutable.ListMap

class ListMapSpec extends MonocleSuite {
  implicit val eqListMap: Eq[ListMap[Int, String]] = Eq.fromUniversalEquals

  checkAll("each Map", EachTests[ListMap[Int, String], String])
}
