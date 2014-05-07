package monocle.function

import monocle.TestUtil._
import monocle.TraversalLaws
import monocle.function.Head._
import org.specs2.scalaz.Spec


class HeadSpec extends Spec {

  checkAll("head List"  , TraversalLaws(head[List[Int]  , Int]))
  checkAll("head Stream", TraversalLaws(head[Stream[Int], Int]))
  checkAll("head String", TraversalLaws(head[String     , Char]))
  checkAll("head Vector", TraversalLaws(head[Vector[Int], Int]))
  checkAll("head Stream", TraversalLaws(head[Stream[Int], Int]))

}
