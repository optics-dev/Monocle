package monocle.syntax

import monocle._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers
import monocle.syntax.all._

class AtSyntaxTest extends AnyFunSuite with Matchers {

  type From = Map[Int, String]
  type To   = Option[String]

  test("optic") {
    val optic = OpticGen[From]()
    optic.iso.at(2) mustBe a[Lens[_, _]]
    optic.prism.at(2) mustBe a[Optional[_, _]]
    optic.lens.at(2) mustBe a[Lens[_, _]]
    optic.optional.at(2) mustBe a[Optional[_, _]]
    optic.setter.at(2) mustBe a[Setter[_, _]]
    optic.getter.at(2) mustBe a[Fold[_, _]]
    optic.fold.at(2) mustBe a[Fold[_, _]]
  }

  test("applied optic") {
    val optic = AppliedOpticGen[From](Map.empty)
    optic.iso.at(2) mustBe a[AppliedLens[_, _]]
    optic.prism.at(2) mustBe a[AppliedOptional[_, _]]
    optic.lens.at(2) mustBe a[AppliedLens[_, _]]
    optic.optional.at(2) mustBe a[AppliedOptional[_, _]]
    optic.setter.at(2) mustBe a[AppliedSetter[_, _]]
    optic.getter.at(2) mustBe a[AppliedFold[_, _]]
    optic.fold.at(2) mustBe a[AppliedFold[_, _]]
  }
}
