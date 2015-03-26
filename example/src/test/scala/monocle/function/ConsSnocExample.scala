package monocle.function

import monocle.std._
import org.specs2.scalaz.Spec


class ConsSnocExample extends Spec {

  "cons add an element to the head" in {
    _cons(1, List(2, 3))        ==== List(1, 2, 3)
    _cons(1, Vector.empty[Int]) ==== Vector(1)
  }

  "uncons deconstructs an element between its head and tail" in {
    _uncons(List(1, 2, 3))   ==== Some((1, List(2, 3)))
    _uncons(Vector(1, 2, 3)) ==== Some((1, Vector(2, 3)))

    _uncons(List.empty[Int]) ==== None
  }

  "snoc add an element to the end" in {
    _snoc(List(1, 2), 3)        ==== List(1, 2, 3)
    _snoc(Vector.empty[Int], 1) ==== Vector(1)
  }

  "snoc deconstructs an element between its init and last" in {
    _unsnoc(List(1, 2, 3))   ==== Some((List(1, 2), 3))
    _unsnoc(Vector(1, 2, 3)) ==== Some((Vector(1, 2), 3))

    _unsnoc(List.empty[Int]) ==== None
  }

}
