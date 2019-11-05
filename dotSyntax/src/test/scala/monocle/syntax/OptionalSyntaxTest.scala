package monocle.syntax

import monocle.syntax.all._
import monocle.Lens
import monocle.Optional
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class OptionalSyntaxTest extends AnyFunSuite with Matchers {
  case class Foo(map: Map[Int, String])

  val foo = Foo(
    map = Map(1 -> "One", 2 -> "Two")
  )

  val map: Lens[Foo, Map[Int, String]] = Lens[Foo, Map[Int, String]](_.map)((foo, newV) => foo.copy(map = newV))
  val opt1: Lens[Foo, Option[String]] = Lens[Foo, Option[String]](_.map.get(1))(
    (foo, newV) => newV.map(v => foo.copy(map = foo.map + (1 -> v))).getOrElse(foo)
  )
  val optional1: Optional[Foo, String] = opt1.compose(Optional.possible[Option[String], String])

  test("basic") {
    optional1.getOption(foo) shouldEqual foo.map.get(1)
    foo.optic(optional1).getOption shouldEqual foo.map.get(1)
  }

  test("possible") {
    opt1.possible.getOption(foo) shouldEqual foo.map.get(1)
  }
}
