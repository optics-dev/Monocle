package monocle.refined

import eu.timepit.refined.W
import eu.timepit.refined.scalacheck.refTypeCogen
import eu.timepit.refined.scalacheck.string.{endsWithArbitrary, startsWithArbitrary}
import monocle.MonocleSuite
import monocle.law.discipline.PrismTests

import cats.Eq

class StringsSpec extends MonocleSuite {
  implicit val eqStartsWith: Eq[StartsWithString[W.`"hello"`.T]] =
    Eq.fromUniversalEquals[StartsWithString[W.`"hello"`.T]]
  implicit val eqEndsWith: Eq[EndsWithString[W.`"world"`.T]] = Eq.fromUniversalEquals[EndsWithString[W.`"world"`.T]]

  checkAll("starts with", PrismTests(startsWith("hello")))
  checkAll("ends with", PrismTests(endsWith("world")))
}
