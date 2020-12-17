package monocle.std

import monocle.MonocleSuite
import monocle.law.discipline.PrismTests
import monocle.law.discipline.function.{EachTests, PossibleTests}

import scala.util.Try
import cats.Eq

import scala.annotation.nowarn

class TrySpec extends MonocleSuite {
  implicit private def tryEqual[A]: Eq[Try[A]] =
    Eq.fromUniversalEquals[Try[A]]

  implicit private def throwableEqual[A]: Eq[Throwable] =
    Eq.fromUniversalEquals[Throwable]

  checkAll("trySuccess", PrismTests(monocle.std.utilTry.trySuccess[Int]))
  checkAll("tryFailure", PrismTests(monocle.std.utilTry.tryFailure[Int]))
  checkAll("each Try", EachTests[Try[Int], Int])
  checkAll("possible Try", PossibleTests[Try[Int], Int]): @nowarn
}
