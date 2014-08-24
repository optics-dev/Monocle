package monocle.std

import monocle.TestUtil._
import monocle.function._
import monocle.std.set._
import monocle.{PrismLaws, TraversalLaws}
import org.specs2.scalaz.Spec


class SetSpec extends Spec {

  checkAll("at Set", TraversalLaws(at[Set[Int], Int, Unit](2)))

  checkAll("empty Set", PrismLaws(empty[Set[Int]]))

}
