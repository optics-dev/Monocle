package monocle.function

import monocle.Iso
import monocle.TestUtil._
import monocle.function.Reverse._
import org.specs2.scalaz.Spec


class ReverseSpec extends Spec {

  checkAll("reverse List"   , Iso.laws(reverse[List[Int]]))
  checkAll("reverse Stream" , Iso.laws(reverse[Stream[Int]]))
  checkAll("reverse String" , Iso.laws(reverse[String]))

}
