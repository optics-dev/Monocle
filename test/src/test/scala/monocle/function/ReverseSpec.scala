package monocle.function

import monocle.IsoLaws
import monocle.TestUtil._
import monocle.function.Reverse._
import org.specs2.scalaz.Spec
import scalaz.Tree

class ReverseSpec extends Spec {

  checkAll("reverse List"  , IsoLaws(reverse[List[Int]  ,List[Int]]))
  checkAll("reverse Vector", IsoLaws(reverse[Vector[Int], Vector[Int]]))
  checkAll("reverse Stream", IsoLaws(reverse[Stream[Int],Stream[Int]]))
  checkAll("reverse String", IsoLaws(reverse[String     , String]))
  checkAll("reverse Tree"  , IsoLaws(reverse[Tree[Int]  , Tree[Int]]))

  checkAll("reverse 2-tuple", IsoLaws(reverse[(Int, Char)         , (Char, Int)]))
  checkAll("reverse 3-tuple", IsoLaws(reverse[(Int, Char, Boolean), (Boolean, Char, Int)]))

}
