package monocle.generic

import monocle.law.discipline.IsoTests
import monocle.law.discipline.function.EachTests
import monocle.generic.all._
import org.scalacheck.Arbitrary
import cats.Eq
import munit.DisciplineSuite

import scala.annotation.nowarn

@nowarn
class ProductSpec extends DisciplineSuite {
  case class Person(name: String, age: Int)

  implicit val personEq: Eq[Person] = Eq.fromUniversalEquals
  implicit val personArb: Arbitrary[Person] = Arbitrary(for {
    n <- Arbitrary.arbitrary[String]
    a <- Arbitrary.arbitrary[Int]
  } yield Person(n, a))

  case class Permissions(read: Boolean, write: Boolean, execute: Boolean)

  implicit val nameEq: Eq[Permissions] = Eq.fromUniversalEquals
  implicit val nameArb: Arbitrary[Permissions] = Arbitrary(for {
    f <- Arbitrary.arbitrary[Boolean]
    l <- Arbitrary.arbitrary[Boolean]
    i <- Arbitrary.arbitrary[Boolean]
  } yield Permissions(f, l, i))

  checkAll("toTuple", IsoTests(product.productToTuple[Person]))

  checkAll("eachTuple2", EachTests[(String, String), String])
  checkAll("eachTuple4", EachTests[(Int, Int, Int, Int), Int])
  checkAll("eachCaseClass", EachTests[Permissions, Boolean])
}
