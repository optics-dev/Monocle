package monocle

import monocle.law.discipline._
import monocle.macros.GenIso
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary._

import scalaz.std.anyVal._
import scalaz.{Category, Compose, Equal, Split}

class IsoSpec extends MonocleSuite {

  val _nullary: Iso[Nullary, Unit] = Iso[Nullary, Unit](n => ()) {
    case () => Nullary()
  }
  val _unary: Iso[Unary, Int] = Iso[Unary, Int](_.i)(Unary)
  val _binary: Iso[Binary, (String, Int)] =
    Iso[Binary, (String, Int)](b => (b.s, b.i))(Binary.tupled)
  val _quintary: Iso[Quintary, (Any, Boolean, String, Int, Double)] =
    Iso[Quintary, (Any, Boolean, String, Int, Double)](
      b => (b.a, b.b, b.s, b.i, b.f))(
      Quintary.tupled)

  case class IntWrapper(i: Int)
  implicit val intWrapperGen: Arbitrary[IntWrapper] = Arbitrary(arbitrary[Int].map(IntWrapper.apply))
  implicit val intWrapperEq = Equal.equalA[IntWrapper]

  case class IdWrapper[A](value: A)
  implicit def idWrapperGen[A: Arbitrary]: Arbitrary[IdWrapper[A]] = Arbitrary(arbitrary[A].map(IdWrapper.apply))
  implicit def idWrapperEq[A: Equal]: Equal[IdWrapper[A]] = Equal.equalA

  case object AnObject
  implicit val anObjectGen: Arbitrary[AnObject.type] = Arbitrary(Gen.const(AnObject))
  implicit val anObjectEq = Equal.equalA[AnObject.type]

  case class EmptyCase()
  implicit val emptyCaseGen: Arbitrary[EmptyCase] = Arbitrary(Gen.const(EmptyCase()))
  implicit val emptyCaseEq = Equal.equalA[EmptyCase]

  case class EmptyCaseType[A]()
  implicit def emptyCaseTypeGen[A]: Arbitrary[EmptyCaseType[A]] = Arbitrary(Gen.const(EmptyCaseType()))
  implicit def emptyCaseTypeEq[A] = Equal.equalA[EmptyCaseType[A]]

  val iso = Iso[IntWrapper, Int](_.i)(IntWrapper.apply)

  checkAll("apply Iso", IsoTests(iso))
  checkAll("GenIso", IsoTests(GenIso[IntWrapper, Int]))
  checkAll("GenIso with type param", IsoTests(GenIso[IdWrapper[Int], Int]))
  checkAll("GenIso.unit object", IsoTests(GenIso.unit[AnObject.type]))
  checkAll("GenIso.unit empty case class", IsoTests(GenIso.unit[EmptyCase]))
  checkAll("GenIso.unit empty case class with type param", IsoTests(GenIso.unit[EmptyCaseType[Int]]))

  checkAll("Iso id", IsoTests(Iso.id[Int]))

  checkAll("Iso.asLens"     , LensTests(iso.asLens))
  checkAll("Iso.asPrism"    , PrismTests(iso.asPrism))
  checkAll("Iso.asOptional" , OptionalTests(iso.asOptional))
  checkAll("Iso.asTraversal", TraversalTests(iso.asTraversal))
  checkAll("Iso.asSetter"   , SetterTests(iso.asSetter))

  checkAll("first" , IsoTests(iso.first[Boolean]))
  checkAll("second", IsoTests(iso.second[Boolean]))
  checkAll("left"  , IsoTests(iso.left[Boolean]))
  checkAll("right" , IsoTests(iso.right[Boolean]))

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

  test("apply") {
    _nullary() shouldEqual Nullary()
    _unary(3) shouldEqual Unary(3)
    _binary("foo", 7) shouldEqual Binary("foo", 7)
    _quintary((), true, "bar", 13, 0.4) shouldEqual
      Quintary((), true, "bar", 13, 0.4)
  }
}

