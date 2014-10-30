package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.law.{LensLaws, OptionalLaws, PrismLaws, TraversalLaws}
import scalaz.==>>
import org.specs2.scalaz.Spec

class IMapSpec extends Spec {

  checkAll("at ==>>", LensLaws(at[Int ==>> String, Int, String](2)))

  checkAll("each ==>>", TraversalLaws(each[Int ==>> String, String]))

  checkAll("empty ==>>", PrismLaws(empty[Int ==>> String]))

  checkAll("filterIndex ==>>", TraversalLaws(filterIndex[Int ==>> Char, Int, Char](_ % 2 == 0)))

  checkAll("index ==>>", OptionalLaws(index[Int ==>> String, Int, String](3)))

}
