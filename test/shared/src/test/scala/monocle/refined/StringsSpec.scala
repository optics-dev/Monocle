package monocle.refined

import eu.timepit.refined._
import eu.timepit.refined.scalacheck.string.{endsWithArbitrary, startsWithArbitrary}
import monocle.MonocleSuite
import monocle.law.discipline.PrismTests
import org.scalacheck.Cogen

import cats.{Eq => Equal}


class StringsSpec extends MonocleSuite {

  implicit val startsWithCoGen: Cogen[StartsWithString[W.`"hello"`.T]] = Cogen[String].contramap[StartsWithString[W.`"hello"`.T]](_.value)
  implicit val endsWithCoGen: Cogen[EndsWithString[W.`"world"`.T]] = Cogen[String].contramap[EndsWithString[W.`"world"`.T]](_.value)

  implicit val eqStartsWith: Equal[StartsWithString[W.`"hello"`.T]] = Equal.fromUniversalEquals[StartsWithString[W.`"hello"`.T]]
  implicit val eqEndsWith: Equal[EndsWithString[W.`"world"`.T]] = Equal.fromUniversalEquals[EndsWithString[W.`"world"`.T]]

  checkAll("starts with", PrismTests(startsWith("hello")))
  checkAll("ends with", PrismTests(endsWith("world")))

}
