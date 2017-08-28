package monocle.refined

import eu.timepit.refined.W
import eu.timepit.refined.scalacheck.refTypeCogen
import eu.timepit.refined.scalacheck.string.{endsWithArbitrary, startsWithArbitrary}
import monocle.MonocleSuite
import monocle.law.discipline.PrismTests

import cats.{Eq => Equal}


class StringsSpec extends MonocleSuite {

  implicit val eqStartsWith: Equal[StartsWithString[W.`"hello"`.T]] = Equal.fromUniversalEquals[StartsWithString[W.`"hello"`.T]]
  implicit val eqEndsWith: Equal[EndsWithString[W.`"world"`.T]] = Equal.fromUniversalEquals[EndsWithString[W.`"world"`.T]]

  checkAll("starts with", PrismTests(startsWith("hello")))
  checkAll("ends with", PrismTests(endsWith("world")))

}
