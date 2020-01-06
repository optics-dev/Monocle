package monocle.syntax

import monocle._
import monocle.syntax.all._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers

class SomeSyntaxTest extends AnyFunSuite with Matchers {

  type From = Option[Int]
  type To   = Option[Int]

  test("optic") {
    val optic = OpticGen[From]()
    optic.iso.some mustBe a[Prism[_, _]]
    optic.prism.some mustBe a[Prism[_, _]]
    optic.lens.some mustBe a[Optional[_, _]]
    optic.optional.some mustBe a[Optional[_, _]]
    optic.setter.some mustBe a[Setter[_, _]]
    optic.getter.some mustBe a[Fold[_, _]]
    optic.fold.some mustBe a[Fold[_, _]]
  }

  test("applied optic") {
    val optic = AppliedOpticGen[From](None)
    optic.iso.some mustBe a[AppliedPrism[_, _]]
    optic.prism.some mustBe a[AppliedPrism[_, _]]
    optic.lens.some mustBe a[AppliedOptional[_, _]]
    optic.optional.some mustBe a[AppliedOptional[_, _]]
    optic.setter.some mustBe a[AppliedSetter[_, _]]
    optic.getter.some mustBe a[AppliedFold[_, _]]
    optic.fold.some mustBe a[AppliedFold[_, _]]
  }
}
