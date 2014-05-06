package monocle.function

import monocle.IsoLaws
import monocle.TestUtil._
import monocle.function.Reverse._
import org.specs2.scalaz.Spec
import scalaz.Tree

class ReverseSpec extends Spec {

  checkAll("reverse List"  , IsoLaws(reverse[List[Int]]))
  checkAll("reverse Stream", IsoLaws(reverse[Stream[Int]]))
  checkAll("reverse String", IsoLaws(reverse[String]))
  checkAll("reverse Tree"  , IsoLaws(reverse[Tree[Int]]))
  checkAll("reverse Vector", IsoLaws(reverse[Vector[Int]]))

}
