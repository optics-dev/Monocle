package monocle

import monocle.macros.GenLens
import monocle.macros.syntax.lens._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class LensGenSpec extends AnyFunSuite with Matchers {

  case class Foo(i: Int, s: String)
  val foo = Foo(5, "Hello")

  test("GenLens") {
    GenLens[Foo](_.i).get(foo) shouldEqual foo.i
  }

  test("AppliedLens") {
    foo.lens(_.i).get shouldEqual foo.i
  }

}
