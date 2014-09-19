package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.{IsoLaws, LensLaws, OptionalLaws, TraversalLaws}
import org.specs2.scalaz.Spec

import scalaz.OneAnd


class OneAndSpec extends Spec {

  checkAll("each OneAnd", TraversalLaws(each[OneAnd[List, Int], Int]))

  checkAll("index OneAnd", OptionalLaws(index[OneAnd[List, Int], Int, Int](1)))

  checkAll("hcons OneAnd", IsoLaws(hcons[OneAnd[List, Int], Int, List[Int]]))

}
