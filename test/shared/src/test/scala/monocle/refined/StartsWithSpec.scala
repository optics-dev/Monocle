package monocle.refined

import monocle.MonocleSuite
import monocle.law.discipline.PrismTests
import org.scalacheck.Cogen
import eu.timepit.refined.scalacheck.string.startsWithArbitrary
import eu.timepit.refined._


import scalaz.Equal


class StartsWithSpec extends MonocleSuite {

  implicit val startsWithCoGen: Cogen[StartsWithString[W.`"hello"`.T]] = Cogen[String].contramap[StartsWithString[W.`"hello"`.T]](_.value)

  implicit val eqStartsWith: Equal[StartsWithString[W.`"hello"`.T]] = Equal.equalA[StartsWithString[W.`"hello"`.T]]

  implicit val eqString: Equal[String] = Equal.equalA[String]

  checkAll("starts with", PrismTests(startsWith("hello")))

}
