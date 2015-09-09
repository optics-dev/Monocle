package monocle.function

import monocle.MonocleSuite

class ConsSnocExample extends MonocleSuite {

  test("cons add an element to the head") {
    _cons(1, List(2, 3))        shouldEqual List(1, 2, 3)
    _cons(1, Vector.empty[Int]) shouldEqual Vector(1)
  }

  test("uncons deconstructs an element between its head and tail") {
    _uncons(List(1, 2, 3))   shouldEqual Some((1, List(2, 3)))
    _uncons(Vector(1, 2, 3)) shouldEqual Some((1, Vector(2, 3)))

    _uncons(List.empty[Int]) shouldEqual None
  }

  test("snoc add an element to the end") {
    _snoc(List(1, 2), 3)        shouldEqual List(1, 2, 3)
    _snoc(Vector.empty[Int], 1) shouldEqual Vector(1)
  }

  test("snoc deconstructs an element between its init and last") {
    _unsnoc(List(1, 2, 3))   shouldEqual Some((List(1, 2), 3))
    _unsnoc(Vector(1, 2, 3)) shouldEqual Some((Vector(1, 2), 3))

    _unsnoc(List.empty[Int]) shouldEqual None
  }

}
