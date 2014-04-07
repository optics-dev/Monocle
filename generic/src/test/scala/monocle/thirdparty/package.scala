package monocle

import monocle.thirdparty.hlist._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import scalaz.Equal
import shapeless.{Generic, HNil}


package object thirdparty {

  case class Example(i: Int, s: String)
  type IntStringHList = HL[Int, HL[String, HNil]]

  implicit val gen = Generic.product[Example]

  implicit val arbitraryExample = Arbitrary(for {
    i <- arbitrary[Int]
    s <- arbitrary[String]
  } yield Example(i, s))

  implicit val arbitraryHListIntString: Arbitrary[IntStringHList] =
    Arbitrary(arbitrary[Example].map(toHList[Example, IntStringHList].get))

  implicit val equalExample        = Equal.equalA[Example]
  implicit val equalHListIntString = Equal.equalA[IntStringHList]

}
