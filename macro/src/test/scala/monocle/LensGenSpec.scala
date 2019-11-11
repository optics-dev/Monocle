package monocle

import monocle.macros.GenLens
import monocle.syntax.all._
import monocle.macros.syntax._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import monocle.macros.GenPrism

class LensGenSpec extends AnyFunSuite with Matchers {

  case class Foo(i: Int, bar: Bar)
  case class Bar(b: Boolean, s: String)
  val foo = Foo(5, Bar(true, "Hello"))

  sealed trait ThisOrThat
  case class This(i: Int, bar: Bar) extends ThisOrThat
  case class That(d: Double) extends ThisOrThat

  val prism = GenPrism[ThisOrThat, This]
  val x = This(5, Bar(true, "Hello"))
  val thisOrThat: ThisOrThat = x

  test("GenLens") {
    GenLens[Foo](_.i).get(foo) shouldEqual foo.i
    GenLens[Foo](_.bar.b).get(foo) shouldEqual foo.bar.b
  }

  test("fields (AppliedIso)") {
    foo.optic.field(_.i).get shouldEqual foo.i
    foo.optic.field(_.bar.b).get shouldEqual foo.bar.b
    foo.optic.field(_.bar).field(_.b).get shouldEqual foo.bar.b
  }

  test("fields (AppliedPrism)") {
    thisOrThat.optic(prism).field(_.i).getOption shouldEqual Some(x.i)
    thisOrThat.optic(prism).field(_.bar.b).getOption shouldEqual Some(x.bar.b)
    thisOrThat.optic(prism).field(_.bar).field(_.b).getOption shouldEqual Some(x.bar.b)
  }

}
