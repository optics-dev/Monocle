package monocle.unsafe

import monocle.MonocleSuite
import monocle.law.discipline.OptionalTests
import monocle.macros.GenLens
import org.scalacheck.Arbitrary

import cats.Eq

class UnsafeSelectSpec extends MonocleSuite {
  /*
    This fails the "unsafe.Prism.round trip other way" test with value -1
    checkAll("unsafe", PrismTests(UnsafeSelect.unsafeSelect((a: Int) => a > Int.MaxValue / 2)))
   */

  test("should fail round trip") {
    val prism    = UnsafeSelect.unsafeSelect((a: Int) => a > 0)
    val valueOk  = 1
    val valueBad = -1
    assertEquals(prism.getOption(prism.reverseGet(valueOk)), Some(valueOk))
    assertEquals(prism.getOption(prism.reverseGet(valueBad)), None)
  }

  test("Predicate should work") {
    val p: Int => Boolean = _ > 10
    val prism             = UnsafeSelect.unsafeSelect(p)

    assertEquals(prism.getOption(12), Some(12))
    assertEquals(prism.getOption(8), None)
  }

  case class Person(name: String, age: Int)

  implicit val personEq: Eq[Person] = Eq.fromUniversalEquals

  implicit val personGen: Arbitrary[Person] = Arbitrary(for {
    name <- Arbitrary.arbitrary[String]
    age  <- Arbitrary.arbitrary[Int]
  } yield Person(name, age))

  checkAll(
    "unsafe legal",
    OptionalTests(UnsafeSelect.unsafeSelect[Person](_.age >= 18).andThen(GenLens[Person](_.name)))
  )
}
