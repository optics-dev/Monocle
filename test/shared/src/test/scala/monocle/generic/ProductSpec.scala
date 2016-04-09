package monocle.generic

import monocle.MonocleSuite
import monocle.law.discipline.IsoTests
import monocle.law.discipline.function.EachTests
import org.scalacheck.Arbitrary

import scalaz.Equal

class ProductSpec extends MonocleSuite {

  case class Person(name: String, age: Int)

  implicit val personEq: Equal[Person] = Equal.equalA
  implicit val personArb: Arbitrary[Person] = Arbitrary(for {
    n <- Arbitrary.arbitrary[String]
    a <- Arbitrary.arbitrary[Int]
  } yield Person(n, a))

  case class Permissions(read: Boolean, write: Boolean, execute: Boolean)

  implicit val nameEq: Equal[Permissions] = Equal.equalA
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
