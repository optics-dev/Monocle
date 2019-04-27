package monocle.unsafe

import monocle.MonocleSuite
import monocle.law.discipline.OptionalTests
import monocle.macros.GenLens
import org.scalacheck.Arbitrary
import scalaz.Equal


class UnsafeSelectSpec extends MonocleSuite {
  /*
    This fails the "unsafe.Prism.round trip other way" test with value -1
    checkAll("unsafe", PrismTests(UnsafeSelect.unsafeSelect((a: Int) => a > Int.MaxValue / 2)))
   */

  test("should fail round trip") {
    val prism = UnsafeSelect.unsafeSelect((a: Int) => a > 0)
    val valueOk = 1
    val valueBad = -1
    prism.getOption(prism.reverseGet(valueOk)) shouldEqual Some(valueOk)
    prism.getOption(prism.reverseGet(valueBad)) shouldEqual None
  }

  test("Predicate should work") {
    val p: Int => Boolean = _ > 10
    val prism = UnsafeSelect.unsafeSelect(p)

    prism.getOption(12) shouldEqual Some(12)
    prism.getOption(8) shouldEqual None
  }

  case class Person(name: String, age: Int)

  implicit val personEq: Equal[Person] = Equal.equalA

  implicit val personGen: Arbitrary[Person] = Arbitrary(for {
    name <- Arbitrary.arbitrary[String]
    age <- Arbitrary.arbitrary[Int]
  } yield Person(name, age))

  checkAll("unsafe legal", OptionalTests(UnsafeSelect.unsafeSelect[Person](_.age >= 18) composeLens GenLens[Person](_.name)))
}
