package monocle.thirdparty

import monocle.Lens
import monocle.TestUtil._
import monocle.thirdparty.tree._
import org.specs2.scalaz.Spec


class TreeSpec extends Spec {

  checkAll("rootLabel"     , Lens.laws(rootLabel[Int]))
  checkAll("subForest"     , Lens.laws(subForest[Int]))
  checkAll("leftMostLabel" , Lens.laws(leftMostLabel[Int]))
  checkAll("rightMostLabel", Lens.laws(rightMostLabel[Int]))

}
