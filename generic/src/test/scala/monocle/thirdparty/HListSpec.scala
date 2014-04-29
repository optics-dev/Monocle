package monocle.thirdparty

import monocle.TestUtil._
import monocle.function.Fields._
import monocle.thirdparty.hlist._
import monocle.{Iso, Lens}
import org.scalacheck.Arbitrary
import org.specs2.scalaz.Spec
import scalaz.Equal
import scalaz.syntax.equal._
import shapeless.{Generic, ::, HNil}
import shapeless.contrib.scalaz._
import shapeless.contrib.scalacheck._
import shapeless.TypeClass.deriveConstructors

class HListSpec extends Spec {

  case class Example(i: Int, b: Boolean, c: Char, f: Float, l: Long, d: Double)

  type H = Int :: Boolean :: Char :: Float :: Long :: Double :: HNil

  implicit val exampleGen: Generic.Aux[Example, H] = Generic.product[Example]

  implicit val hEq: Equal[H] = new Equal[H] {
    def equal(a1: H, a2: H): Boolean =
      fromHList[H, Example].get(a1) === fromHList[H,Example].get(a2)
  }

  implicit val hArb: Arbitrary[H] = Arbitrary(for {
    example <- Arbitrary.arbitrary[Example]
  } yield toHList[Example, H].get(example))


  checkAll("_1 from HList", Lens.laws(_1[H, Int]))
  checkAll("_2 from HList", Lens.laws(_2[H, Boolean]))
  checkAll("_3 from HList", Lens.laws(_3[H, Char]))
  checkAll("_4 from HList", Lens.laws(_4[H, Float]))
  checkAll("_5 from HList", Lens.laws(_5[H, Long]))
  checkAll("_6 from HList", Lens.laws(_6[H, Double]))

  checkAll("toHList", Iso.laws(toHList[Example, H]) )

}
