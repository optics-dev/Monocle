//package monocle.refined
//
//import eu.timepit.refined.scalacheck.refTypeCogen
//import eu.timepit.refined.scalacheck.string.{endsWithArbitrary, startsWithArbitrary}
//import eu.timepit.refined.scalacheck.any.arbitraryFromValidate
//import monocle.law.discipline.PrismTests
//import monocle.refined.all._
//import cats.Eq
//
//class StringsSpec extends munit.DisciplineSuite {
//  implicit val eqStartsWith: Eq[StartsWithString["hello"]] =
//    Eq.fromUniversalEquals[StartsWithString["hello"]]
//  implicit val eqEndsWith: Eq[EndsWithString["world"]] = Eq.fromUniversalEquals[EndsWithString["world"]]
//
//  checkAll("starts with", PrismTests(startsWith("hello")))
//  checkAll("ends with", PrismTests(endsWith("world")))
//}
