package monocle.syntax

import monocle.implicits._
import monocle.Lens
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class IsoSyntaxTest extends AnyFunSuite with Matchers {
  case class Foo(map: Map[Int, String], list: List[Int], tuple: (Boolean, String))
  val foo = Foo(
    map = Map(1 -> "One", 2 -> "Two"),
    list = List(1, 2, 3),
    tuple = (false, "hello")
  )

  val list: Lens[Foo, List[Int]]          = Lens[Foo, List[Int]](_.list)((foo, newV) => foo.copy(list = newV))
  val tuple: Lens[Foo, (Boolean, String)] = Lens[Foo, (Boolean, String)](_.tuple)((foo, newV) => foo.copy(tuple = newV))

  test("reverse") {
    foo.optic(list).reverse.get shouldEqual foo.list.reverse
    foo.optic(tuple)._2.reverse.get shouldEqual "hello".reverse
    foo.optic(tuple).reverse.get shouldEqual ("hello", false)
  }
}
