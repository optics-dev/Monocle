package monocle.function

import monocle.TestUtil._
import monocle.Traversal
import monocle.function.Each._
import org.specs2.scalaz.Spec
import scalaz.Tree

class EachSpec extends Spec {

  checkAll("each Map"   , Traversal.laws(each[Map[Int, String], String]))
  checkAll("each Option", Traversal.laws(each[Option[Int], Int]))
  checkAll("each List"  , Traversal.laws(each[List[Int], Int]))
  checkAll("each Stream", Traversal.laws(each[Stream[Int], Int]))
  checkAll("each pair"  , Traversal.laws(each[(Int, Int), Int]))
  checkAll("each Triple", Traversal.laws(each[(Int, Int, Int), Int]))

  checkAll("each Tree"  , Traversal.laws(each[Tree[Int], Int]))

}
