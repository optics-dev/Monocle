package monocle.function

import monocle.PrismLaws
import org.scalacheck.Prop._
import org.scalacheck.{Properties, Arbitrary}
import scalaz.std.tuple._
import scalaz.syntax.equal._

import scalaz.Equal


object ConsSnocLaws {

  def apply[S, A](implicit aEq: Equal[A], aArb: Arbitrary[A],
                           sEq: Equal[S], sArb: Arbitrary[S],
                           evCons: Cons[S, A], evSnoc: Snoc[S, A],
                           evReverse: Reverse[S, S]) = new Properties("Cons - Snoc") {

    include(PrismLaws(_cons[S, A]))
    include(PrismLaws(_snoc[S, A]))

    property("uncons to unsnoc") = forAll { s: S =>
      (for {
        (head, tail)                 <- uncons(s)
        (reversedInit, reversedLast) <- unsnoc(reverse(s))
      } yield {
        head === reversedLast
        tail === reverse(reversedInit)
      }).getOrElse(true)
    }

    property("unsnoc to uncons") = forAll { s: S =>
      (for {
        (init, last)                 <- unsnoc(s)
        (reversedHead, reversedTail) <- uncons(reverse(s))
      } yield {
        last === reversedHead
        init === reverse(reversedTail)
      }).getOrElse(true)
    }

  }

}
