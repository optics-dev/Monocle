package monocle.syntax

import monocle.syntax.all._
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
    list.reverse.get(foo) shouldEqual foo.list.reverse
    tuple._2.reverse.get(foo) shouldEqual "hello".reverse
    tuple.reverse.get(foo) shouldEqual ("hello", false)
  }
}
