//package monocle.refined
//
//import eu.timepit.refined.scalacheck.refTypeCogen
//import eu.timepit.refined.scalacheck.string.{endsWithArbitrary, startsWithArbitrary}
//import eu.timepit.refined.scalacheck.any.arbitraryFromValidate
//import monocle.MonocleSuite
//import monocle.law.discipline.PrismTests
//
//import cats.Eq
//
//class StringsSpec extends MonocleSuite {
//  implicit val eqStartsWith: Eq[StartsWithString["hello"]] =
//    Eq.fromUniversalEquals[StartsWithString["hello"]]
//  implicit val eqEndsWith: Eq[EndsWithString["world"]] = Eq.fromUniversalEquals[EndsWithString["world"]]
//
//  checkAll("starts with", PrismTests(startsWith("hello")))
//  checkAll("ends with", PrismTests(endsWith("world")))
//}
