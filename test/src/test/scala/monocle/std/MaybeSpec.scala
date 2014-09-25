package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.law.{IsoLaws, PrismLaws, TraversalLaws}
import org.specs2.scalaz.Spec

import scalaz.Maybe

class MaybeSpec extends Spec {

  checkAll("maybeToOption", IsoLaws(maybeToOption[Int, Int]))

  checkAll("just"   , PrismLaws(just[Int, Int]))
  checkAll("nothing", PrismLaws(nothing[Long]))

  checkAll("each Maybe", TraversalLaws(each[Maybe[Int], Int]))

}
