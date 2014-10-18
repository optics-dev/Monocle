package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.law.{PrismLaws, LensLaws}
import monocle.std.set._
import scalaz.ISet
import org.specs2.scalaz.Spec


class ISetSpec extends Spec {

  checkAll("at Set", LensLaws(at[ISet[Int], Int, Unit](2)))

  checkAll("empty Set", PrismLaws(empty[ISet[Int]]))

}
