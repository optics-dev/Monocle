package monocle.generic

import monocle.MonocleSuite
import monocle.law.discipline.IsoTests
import org.scalacheck.Arbitrary

import scalaz.Equal

class ProductSpec extends MonocleSuite {

  case class Person(name: String, age: Int)

  implicit val personEq: Equal[Person] = Equal.equalA
  implicit val personArb: Arbitrary[Person] = Arbitrary(for{
    n <- Arbitrary.arbitrary[String]
    a <- Arbitrary.arbitrary[Int]
  } yield Person(n, a))

  checkAll("toTuple", IsoTests(product.productToTuple[Person]))
}
