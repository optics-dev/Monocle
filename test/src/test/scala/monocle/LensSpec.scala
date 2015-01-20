package monocle

import monocle.TestUtil._
import monocle.law.{LensLaws, OptionalLaws, SetterLaws, TraversalLaws}
import monocle.macros.{GenLens, Lenses}
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import org.specs2.scalaz._

import scalaz.Equal

class LensSpec extends Spec {

  @Lenses
  case class Example(s: String, i: Int)

  val stringLens = Lens[Example, String](_.s)(s => ex => ex.copy(s = s))

  implicit val exampleGen: Arbitrary[Example] = Arbitrary(for {
    s <- arbitrary[String]
    i <- arbitrary[Int]
  } yield Example(s, i))

  implicit val exampleEq = Equal.equalA[Example]

  checkAll("apply Lens", LensLaws(stringLens))
  checkAll("GenLens", LensLaws(GenLens[Example](_.s)))
  checkAll("Lenses",  LensLaws(Example.s))

  checkAll("lens.asOptional" , OptionalLaws(stringLens.asOptional))
  checkAll("lens.asTraversal", TraversalLaws(stringLens.asTraversal))
  checkAll("lens.asSetter"   , SetterLaws(stringLens.asSetter))


}
