package monocle.syntax

import monocle.implicits._
import monocle.{Iso, Lens}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class LensSyntaxTest extends AnyFunSuite with Matchers {
  case class Foo(map: Map[Int, String], list: List[Int], tuple: (Boolean, String, Int, Long, Double, (Int, String)))

  val foo = Foo(
    map = Map(1 -> "One", 2 -> "Two"),
    list = List(1, 2, 3),
    tuple = (false, "hello", -1, 4L, 0.5, (1, "world"))
  )

  val map: Lens[Foo, Map[Int, String]] = Lens[Foo, Map[Int, String]](_.map)((foo, newV) => foo.copy(map = newV))
  val list: Lens[Foo, List[Int]]       = Lens[Foo, List[Int]](_.list)((foo, newV) => foo.copy(list = newV))
  val tuple: Lens[Foo, (Boolean, String, Int, Long, Double, (Int, String))] =
    Lens[Foo, (Boolean, String, Int, Long, Double, (Int, String))](_.tuple)((foo, newV) => foo.copy(tuple = newV))

  test("_1") {
    foo.optic(tuple)._1.get shouldEqual foo.tuple._1
    foo.optic(tuple).first.get shouldEqual foo.tuple._1
  }

  test("_2") {
    foo.optic(tuple)._2.get shouldEqual foo.tuple._2
    foo.optic(tuple).second.get shouldEqual foo.tuple._2
  }

  test("_3") {
    foo.optic(tuple)._3.get shouldEqual foo.tuple._3
    foo.optic(tuple).third.get shouldEqual foo.tuple._3
  }

  test("_4") {
    foo.optic(tuple)._4.get shouldEqual foo.tuple._4
    foo.optic(tuple).fourth.get shouldEqual foo.tuple._4
  }

  test("_5") {
    foo.optic(tuple)._5.get shouldEqual foo.tuple._5
    foo.optic(tuple).fifth.get shouldEqual foo.tuple._5
  }

  test("_6") {
    foo.optic(tuple)._6.get shouldEqual foo.tuple._6
    foo.optic(tuple).sixth.get shouldEqual foo.tuple._6
  }

  test("at") {
    foo.optic(map).at(1).get shouldEqual foo.map.get(1)
  }

  test("cons") {
    foo.optic(list).cons.getOption shouldEqual Some((foo.list.head, foo.list.tail))
  }

  test("index") {
    foo.optic(map).index(1).getOption shouldEqual foo.map.get(1)
  }

  test("headOption") {
    foo.optic(list).headOption.getOption shouldEqual foo.list.headOption
  }

  test("tailOption") {
    foo.optic(list).tailOption.getOption shouldEqual Some(foo.list.tail)
  }

  test("nested") {
    val x: List[Map[Int, Either[(Boolean, Char), String]]] = Nil

    val root = Iso.id[List[Map[Int, Either[(Boolean, Char), String]]]]

    root.cons._2.index(3).at(2).some.left._1.getOption(x) shouldEqual None
    x.optic.cons._2.index(3).at(2).some.left._1.getOption shouldEqual None
  }
}
