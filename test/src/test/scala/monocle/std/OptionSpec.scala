package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.{OptionalLaws, TraversalLaws, IsoLaws, PrismLaws}
import org.specs2.scalaz.Spec

class OptionSpec extends Spec {

  checkAll("some", PrismLaws(some[Int, Int]))
  checkAll("none", PrismLaws(none[Long]))

  checkAll("someIso", IsoLaws(someIso[Int, Int]))

  checkAll("each Option", TraversalLaws(each[Option[Int], Int]))

  checkAll("empty Option", PrismLaws(empty[Map[Int, String]]))

  checkAll("headOption Option", OptionalLaws(headOption[Option[Int] , Int]))

  checkAll("lastOption Option", OptionalLaws(lastOption[Option[Int], Int]))

}
