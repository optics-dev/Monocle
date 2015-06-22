package monocle

import monocle.law.discipline._
import monocle.macros.GenIso
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary._

import scalaz.std.anyVal._
import scalaz.{Category, Compose, Equal, Split}

class IsoSpec extends MonocleSuite {

  case class IntWrapper(i: Int)

  implicit val intWrapperGen: Arbitrary[IntWrapper] = Arbitrary(arbitrary[Int].map(IntWrapper.apply))

  implicit val intWrapperEq = Equal.equalA[IntWrapper]

  case object AnObject

  implicit val anObjectGen: Arbitrary[AnObject.type] = Arbitrary(Gen.const(AnObject))

  implicit val anObjectEq = Equal.equalA[AnObject.type]

  val iso = Iso[IntWrapper, Int](_.i)(IntWrapper.apply)

  checkAll("apply Iso", IsoTests(iso))
  checkAll("GenIso", IsoTests(GenIso[IntWrapper, Int]))
  checkAll("GenIso.obj", IsoTests(GenIso.obj[AnObject.type]))

  checkAll("Iso id", IsoTests(Iso.id[Int]))

  checkAll("Iso.asLens"     , LensTests(iso.asLens))
  checkAll("Iso.asPrism"    , PrismTests(iso.asPrism))
  checkAll("Iso.asOptional" , OptionalTests(iso.asOptional))
  checkAll("Iso.asTraversal", TraversalTests(iso.asTraversal))
  checkAll("Iso.asSetter"   , SetterTests(iso.asSetter))


  // test implicit resolution of type classes

  test("Iso has a Compose)stance") {
    Compose[Iso].compose(iso, iso.reverse).get(3) shouldEqual  3
  }

  test("Iso has a Category)stance") {
    Category[Iso].id[Int].get(3) shouldEqual 3
  }

  test("Iso has a Split)stance") {
    Split[Iso].split(iso, iso.reverse).get((IntWrapper(3), 3)) shouldEqual ((3, IntWrapper(3)))
  }


}

