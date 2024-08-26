package monocle.function

import monocle.MonocleSuite

import scala.annotation.nowarn

@nowarn
class ConsSnocExample extends MonocleSuite {
  test("cons add an element to the head") {
    assertEquals(_cons(1, List(2, 3)), List(1, 2, 3))
    assertEquals(_cons(1, Vector.empty[Int]), Vector(1))
  }

  test("uncons deconstructs an element between its head and tail") {
    assertEquals(_uncons(List(1, 2, 3)), Some((1, List(2, 3))))
    assertEquals(_uncons(Vector(1, 2, 3)), Some((1, Vector(2, 3))))

    assertEquals(_uncons(List.empty[Int]), None)
  }

  test("snoc add an element to the end") {
    assertEquals(_snoc(List(1, 2), 3), List(1, 2, 3))
    assertEquals(_snoc(Vector.empty[Int], 1), Vector(1))
  }

  test("snoc deconstructs an element between its init and last") {
    assertEquals(_unsnoc(List(1, 2, 3)), Some((List(1, 2), 3)))
    assertEquals(_unsnoc(Vector(1, 2, 3)), Some((Vector(1, 2), 3)))

    assertEquals(_unsnoc(List.empty[Int]), None)
  }
}
