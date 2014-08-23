package monocle.function

import monocle.{PrismLaws, LensLaws}
import monocle.std._
import monocle.syntax.iso._
import org.scalacheck.Prop._
import org.scalacheck.{Properties, Arbitrary}
import scalaz.std.option._
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

    property("cons == reverse . snoc . reverse") = forAll { s: S =>
      uncons(s) === (s applyIso reverse composePrism  _snoc composePrism reverse).getOption
    }

    property("snoc == reverse . cons . reverse") = forAll { s: S =>
      unsnoc(s) === (s applyIso reverse composePrism  _cons composePrism reverse).getOption
    }

  }

}
