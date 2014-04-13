package monocle.function

import monocle.Iso
import monocle.TestUtil._
import monocle.function.Reverse._
import org.specs2.scalaz.Spec
import scalaz.Tree


class ReverseSpec extends Spec {

  checkAll("reverse List"  , Iso.laws(reverse[List[Int]]))
  checkAll("reverse Stream", Iso.laws(reverse[Stream[Int]]))
  checkAll("reverse String", Iso.laws(reverse[String]))
  checkAll("reverse Tree"  , Iso.laws(reverse[Tree[Int]]))

}
