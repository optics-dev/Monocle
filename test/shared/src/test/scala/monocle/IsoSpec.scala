package monocle

import monocle.law.discipline._
import monocle.macros.GenIso
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary._

import cats.Eq
import cats.arrow.{Category, Compose}

class IsoSpec extends MonocleSuite {
  val _nullary: Iso[Nullary, Unit] = Iso[Nullary, Unit](n => ()) { case () =>
    Nullary()
  }
  val _unary: Iso[Unary, Int] = Iso[Unary, Int](_.i)(Unary)
  val _binary: Iso[Binary, (String, Int)] =
    Iso[Binary, (String, Int)](b => (b.s, b.i))(Binary.tupled)
  val _quintary: Iso[Quintary, (Char, Boolean, String, Int, Double)] =
    Iso[Quintary, (Char, Boolean, String, Int, Double)](b => (b.c, b.b, b.s, b.i, b.f))(Quintary.tupled)

  case class IntWrapper(i: Int)
  implicit val intWrapperGen: Arbitrary[IntWrapper] = Arbitrary(arbitrary[Int].map(IntWrapper.apply))
  implicit val intWrapperEq                         = Eq.fromUniversalEquals[IntWrapper]

  case class IdWrapper[A](value: A)
  implicit def idWrapperGen[A: Arbitrary]: Arbitrary[IdWrapper[A]] =
    Arbitrary(arbitrary[A].map(IdWrapper.apply))
  implicit def idWrapperEq[A: Eq]: Eq[IdWrapper[A]] = Eq.fromUniversalEquals

  case object AnObject
  implicit val anObjectGen: Arbitrary[AnObject.type] = Arbitrary(Gen.const(AnObject))
  implicit val anObjectEq                            = Eq.fromUniversalEquals[AnObject.type]

  case class EmptyCase()
  implicit val emptyCaseGen: Arbitrary[EmptyCase] = Arbitrary(Gen.const(EmptyCase()))
  implicit val emptyCaseEq                        = Eq.fromUniversalEquals[EmptyCase]

  case class EmptyCaseType[A]()
  implicit def emptyCaseTypeGen[A]: Arbitrary[EmptyCaseType[A]] =
    Arbitrary(Gen.const(EmptyCaseType()))
  implicit def emptyCaseTypeEq[A] = Eq.fromUniversalEquals[EmptyCaseType[A]]

  val iso = Iso[IntWrapper, Int](_.i)(IntWrapper.apply)
  val involutedListReverse =
    Iso.involuted[List[Int]](_.reverse) // ∀ {T} -> List(ts: T*).reverse.reverse == List(ts: T*)
  val involutedTwoMinusN = Iso.involuted[Int](2 - _) //  ∀ {n : Int} -> n == 2 - (2 - n)

  checkAll("apply Iso", IsoTests(iso))
  checkAll("GenIso", IsoTests(GenIso[IntWrapper, Int]))
  checkAll("GenIso with type param", IsoTests(GenIso[IdWrapper[Int], Int]))
  checkAll("GenIso.unit object", IsoTests(GenIso.unit[AnObject.type]))
  checkAll("GenIso.unit empty case class", IsoTests(GenIso.unit[EmptyCase]))
  checkAll("GenIso.unit empty case class with type param", IsoTests(GenIso.unit[EmptyCaseType[Int]]))

  checkAll("Iso id", IsoTests(Iso.id[Int]))

  checkAll("Iso involutedListReverse", IsoTests(involutedListReverse))
  checkAll("Iso involutedTwoMinusN", IsoTests(involutedTwoMinusN))

  checkAll("Iso.asLens", LensTests(iso.asLens))
  checkAll("Iso.asPrism", PrismTests(iso.asPrism))
  checkAll("Iso.asOptional", OptionalTests(iso.asOptional))
  checkAll("Iso.asTraversal", TraversalTests(iso.asTraversal))
  checkAll("Iso.asSetter", SetterTests(iso.asSetter))

  checkAll("first", IsoTests(iso.first[Boolean]))
  checkAll("second", IsoTests(iso.second[Boolean]))
  checkAll("left", IsoTests(iso.left[Boolean]))
  checkAll("right", IsoTests(iso.right[Boolean]))

  // test implicit resolution of type classes

  test("Iso has a Compose instance") {
    assertEquals(Compose[Iso].compose(iso, iso.reverse).get(3), 3)
  }

  test("Iso has a Category instance") {
    assertEquals(Category[Iso].id[Int].get(3), 3)
  }

  test("mapping") {
    import cats.Id

    assertEquals(iso.mapping[Id].get(IntWrapper(3)), 3)
    assertEquals(iso.mapping[Id].reverseGet(3), IntWrapper(3))
  }

  test("apply") {
    assertEquals(_nullary(), Nullary())
    assertEquals(_unary(3), Unary(3))
    assertEquals(_binary("foo", 7), Binary("foo", 7))
    assertEquals(_quintary('x', true, "bar", 13, 0.4), Quintary('x', true, "bar", 13, 0.4))
  }

  test("unapply") {
    // format: off
assertEquals(    (Nullary() match { case _nullary(unit) => unit }) ,  (()))
    // format: on
    assertEquals((Unary(3) match { case _unary(value) => value * 2 }), 6)
    assertEquals((Binary("foo", 7) match { case _binary(s, i) => s + i }), "foo7")
    assertEquals(
      (Quintary('x', true, "bar", 13, 0.4) match {
        case _quintary(c, b, s, i, f) => "" + c + b + s + i + f
      }),
      "xtruebar130.4"
    )
  }

  test("get") {
    assertEquals(iso.get(IntWrapper(5)), 5)
  }

  test("reverseGet") {
    assertEquals(iso.reverseGet(5), IntWrapper(5))
  }

  test("find") {
    assertEquals(iso.find(_ > 5)(IntWrapper(9)), Some(9))
    assertEquals(iso.find(_ > 5)(IntWrapper(3)), None)
  }

  test("exist") {
    assertEquals(iso.exist(_ > 5)(IntWrapper(9)), true)
    assertEquals(iso.exist(_ > 5)(IntWrapper(3)), false)
  }

  test("set") {
    assertEquals(iso.replace(5)(IntWrapper(0)), IntWrapper(5))
  }

  test("modify") {
    assertEquals(iso.modify(_ + 1)(IntWrapper(0)), IntWrapper(1))
  }

  test("involuted") {
    assertEquals(involutedListReverse.get(List(1, 2, 3)), List(3, 2, 1))
    assertEquals(involutedListReverse.reverseGet(List(1, 2, 3)), List(3, 2, 1))

    assertEquals(involutedListReverse.reverse.get(List(1, 2, 3)), involutedListReverse.get(List(1, 2, 3)))
    assertEquals(involutedListReverse.reverse.reverseGet(List(1, 2, 3)), involutedListReverse.reverseGet(List(1, 2, 3)))

    assertEquals(involutedTwoMinusN.get(5), -3)
    assertEquals(involutedTwoMinusN.reverseGet(5), -3)

    assertEquals(involutedTwoMinusN.reverse.get(5), involutedTwoMinusN.get(5))
    assertEquals(involutedTwoMinusN.reverse.reverseGet(5), involutedTwoMinusN.reverseGet(5))
  }

  test("GenIso nullary equality".ignore) {
    assertEquals(GenIso.unit[Nullary], _nullary)
  }

  test("GenIso unary equality".ignore) {
    assertEquals(GenIso[Unary, Int], _unary)
  }

  test("GenIso binary equality".ignore) {
    assertEquals(GenIso.fields[Binary], _binary)
  }

  test("GenIso quintary equality".ignore) {
    assertEquals(GenIso.fields[Quintary], _quintary)
  }

  test("to") {
    assertEquals(iso.to(_.toString()).get(IntWrapper(5)), "5")
  }

  test("some") {
    case class SomeTest(y: Option[Int])
    val obj = SomeTest(Some(2))

    val iso = Iso[SomeTest, Option[Int]](_.y)(SomeTest)

    assertEquals(iso.some.getOption(obj), Some(2))
    assertEquals(obj.applyIso(iso).some.getOption, Some(2))
  }

  test("withDefault") {
    case class SomeTest(y: Option[Int])
    val objSome = SomeTest(Some(2))
    val objNone = SomeTest(None)

    val iso = Iso[SomeTest, Option[Int]](_.y)(SomeTest)

    assertEquals(iso.withDefault(0).get(objSome), 2)
    assertEquals(iso.withDefault(0).get(objNone), 0)

    assertEquals(objNone.applyIso(iso).withDefault(0).get, 0)
  }

  test("each") {
    case class SomeTest(y: List[Int])
    val obj = SomeTest(List(1, 2, 3))

    val iso = Iso[SomeTest, List[Int]](_.y)(SomeTest)

    assertEquals(iso.each.getAll(obj), List(1, 2, 3))
    assertEquals(obj.applyIso(iso).each.getAll, List(1, 2, 3))
  }
}
