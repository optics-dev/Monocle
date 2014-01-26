package lens

import scalaz.std.option._

object Example extends App {
  import Address._
  import Location._
  import Person._

  val l = Location(2, 6)
  val a = Address("London", "EC1...", l)
  val p = Person(25, "Roger", a)

  //basic scala
  println(p.copy(_address = p._address.copy(_city = "Paris")))


  println((address >- city).set(p, "Paris") )
  println((address >- city).get(p))
  println((address >- city).modify(p, _ + "!!!"))
  println((address >- city).lift(p, city => Option(city)))

  println(locationTraversal.get(l))
  println(locationTraversal.set(l, 1.0))
  import scalaz.std.anyVal.doubleInstance
  println(locationTraversal.fold(l))
  println(locationTraversal.modify(l, _ + 2))

  import scalaz.std.list._
  println(locationTraversal.lift(l, pos => List(pos + 1, pos, pos - 1)))

  // composition of lens and traversal
  println((address >- location >- locationTraversal).get(p))

}




