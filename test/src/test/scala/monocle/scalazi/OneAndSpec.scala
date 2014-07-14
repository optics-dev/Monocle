package monocle.scalazi

import monocle.TestUtil._
import monocle.function._
import monocle.{LensLaws, OptionalLaws, TraversalLaws}
import org.specs2.scalaz.Spec
import scalaz.OneAnd
import scalaz.std.list._
import monocle.std.list._


class OneAndSpec extends Spec {

  checkAll("each OneAnd", TraversalLaws(each[OneAnd[List, Int], Int]))

  checkAll("index OneAnd", OptionalLaws(index[OneAnd[List, Int], Int, Int](1)))

  checkAll("head OneAnd", LensLaws(head[OneAnd[List, Int], Int]))

  checkAll("tail OneAnd", LensLaws(tail[OneAnd[List, Int], List[Int]]))

  checkAll("lastOption OneAnd", OptionalLaws(lastOption[OneAnd[List, Int], Int]))

}
