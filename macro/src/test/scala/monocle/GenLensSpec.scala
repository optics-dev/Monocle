package monocle

import monocle.macros.GenLens
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class GenLensSpec extends AnyFunSuite with Matchers {

  case class Foo(i: Int, bar: Bar)
  case class Bar(b: Boolean, s: String)
  val foo = Foo(5, Bar(true, "Hello"))

  test("GenLens") {
    GenLens[Foo](_.i).get(foo) shouldEqual foo.i
    GenLens[Foo](_.bar.b).get(foo) shouldEqual foo.bar.b
  }

}
