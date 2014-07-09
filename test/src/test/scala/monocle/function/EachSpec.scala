package monocle.function

import monocle.TestUtil._
import monocle.TraversalLaws
import monocle.function.Each._
import org.specs2.scalaz.Spec
import scalaz.{IList, Tree, OneAnd}
import scalaz.std.list._

class EachSpec extends Spec {

  checkAll("each Map"   , TraversalLaws(each[Map[Int, String], String]))
  checkAll("each Option", TraversalLaws(each[Option[Int], Int]))
  checkAll("each List"  , TraversalLaws(each[List[Int], Int]))
  checkAll("each IList" , TraversalLaws(each[IList[Int], Int]))
  checkAll("each Vector", TraversalLaws(each[Vector[Int], Int]))
  checkAll("each Stream", TraversalLaws(each[Stream[Int], Int]))
  checkAll("each OneAnd", TraversalLaws(each[OneAnd[List, Int], Int]))
  checkAll("each pair"  , TraversalLaws(each[(Int, Int), Int]))
  checkAll("each Triple", TraversalLaws(each[(Int, Int, Int), Int]))

  checkAll("each Tree"  , TraversalLaws(each[Tree[Int], Int]))


}
