package monocle.thirdparty

import monocle.Lens
import monocle.TestUtil._
import monocle.thirdparty.tree._
import org.specs2.scalaz.Spec


class TreeSpec extends Spec {

  checkAll("leftMostNode" , Lens.laws(leftMostNode[Int]))
  checkAll("rightMostNode", Lens.laws(rightMostNode[Int]))

}
