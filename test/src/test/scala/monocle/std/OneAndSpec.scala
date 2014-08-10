package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.{LensLaws, OptionalLaws, TraversalLaws}
import org.specs2.scalaz.Spec

import scalaz.OneAnd


class OneAndSpec extends Spec {

  checkAll("each OneAnd", TraversalLaws(each[OneAnd[List, Int], Int]))

  checkAll("index OneAnd", OptionalLaws(index[OneAnd[List, Int], Int, Int](1)))

  checkAll("head OneAnd", LensLaws(head[OneAnd[List, Int], Int]))

  checkAll("tail OneAnd", LensLaws(tail[OneAnd[List, Int], List[Int]]))

  checkAll("last OneAnd", LensLaws(last[OneAnd[List, Int], Int]))

}
