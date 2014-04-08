package monocle.function

import monocle.TestUtil._
import monocle.Traversal
import monocle.function.Index._
import org.specs2.scalaz.Spec


class IndexSpec extends Spec {

  checkAll("index Map" , Traversal.laws(index[Map[Int, String], Int, String](3)))

  checkAll("index List", Traversal.laws(index[List[String], Int, String](2)))

}
