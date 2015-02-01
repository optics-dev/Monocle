package monocle

import monocle.TestUtil._
import monocle.law.{OptionalLaws, PrismLaws, SetterLaws, TraversalLaws}
import org.specs2.scalaz.Spec

import scalaz._

class PrismSpec extends Spec {

  def right[E, A]: Prism[E \/ A, A] = Prism[E \/ A, A](_.toMaybe)(\/.right)

  checkAll("apply Prism", PrismLaws(right[String, Int]))

  checkAll("prism.asTraversal", OptionalLaws(right[String, Int].asOptional))
  checkAll("prism.asTraversal", TraversalLaws(right[String, Int].asTraversal))
  checkAll("prism.asSetter"   , SetterLaws(right[String, Int].asSetter))

  // test implicit resolution of type classes

  "Prism has a Compose instance" in {
    Compose[Prism].compose(right[String, Int], right[String, String \/ Int]).getMaybe(\/-(\/-(3))) ==== Maybe.just(3)
  }

  "Prism has a Category instance" in {
    Category[Prism].id[Int].getMaybe(3) ==== Maybe.just(3)
  }


}