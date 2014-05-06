package monocle.thirdparty

import monocle.LensLaws
import monocle.TestUtil._
import monocle.thirdparty.tree._
import org.specs2.scalaz.Spec


class TreeSpec extends Spec {

  checkAll("rootLabel"     , LensLaws(rootLabel[Int]))
  checkAll("subForest"     , LensLaws(subForest[Int]))
  checkAll("leftMostLabel" , LensLaws(leftMostLabel[Int]))
  checkAll("rightMostLabel", LensLaws(rightMostLabel[Int]))

}
