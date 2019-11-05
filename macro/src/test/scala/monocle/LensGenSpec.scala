package monocle

import monocle.macros.GenLens
import monocle.macros.syntax.lens._
import monocle.syntax.all._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class LensGenSpec extends AnyFunSuite with Matchers {

  case class Foo(i: Int, bar: Bar)
  case class Bar(b: Boolean, s: String)
  val foo = Foo(5, Bar(true, "Hello"))

  test("GenLens") {
    GenLens[Foo](_.i).get(foo) shouldEqual foo.i
//    GenLens[Foo](_.bar.b).get(foo) shouldEqual foo.bar.b
  }

  test("fields") {
    foo.optic.field(_.i).get shouldEqual foo.i
//    foo.optic.field(_.bar.b).get shouldEqual foo.bar.b
    foo.optic.field(_.bar).field(_.b).get shouldEqual foo.bar.b
  }

}
