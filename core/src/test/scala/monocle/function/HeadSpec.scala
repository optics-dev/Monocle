package monocle.function

import monocle.TestUtil._
import monocle.Traversal
import monocle.function.Head._
import org.specs2.scalaz.Spec


class HeadSpec extends Spec {

  checkAll("head List"  , Traversal.laws(head[List[Int]  , Int]))
  checkAll("head Stream", Traversal.laws(head[Stream[Int], Int]))
  checkAll("head String", Traversal.laws(head[String     , Char]))
  checkAll("head Vector", Traversal.laws(head[Vector[Int], Int]))

}
