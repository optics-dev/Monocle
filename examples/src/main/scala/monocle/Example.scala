package monocle

import monocle.ExampleInstances._
import scalaz.std.AllInstances._

object Example extends App {
  import Address._
  import Location._
  import Person._

  val l = Location(2, 6)
  val a = Address("London", "EC1...", l)
  val p = Person(25, "Roger", a)

  //basic scala
  println(p.copy(_address = p._address.copy(_city = "Paris")))

  println((address compose city).set(p, "Paris") )
  println((address compose city).get(p))
  println((address compose city).modify(p, _ + "!!!"))
  println((address compose city).lift(p, city => Option(city)))

  println(locationTraversal.toListOf(l))
  println(locationTraversal.set(l, 1.0))
  println(locationTraversal.fold(l))
  println(locationTraversal.modify(l, _ + 2))

  println(locationTraversal.multiLift(l, pos => List(pos + 1, pos, pos - 1)))

  // composition of lenses and traversal
  println((address compose location compose locationTraversal).toListOf(p))

  val int2DoubleOption = Traversal[Option, Int, Double]

  val someInt : Option[Int] = Some(1)
  println(int2DoubleOption.modify(someInt, _ + 2.00))
  println(int2DoubleOption.toListOf(someInt))

  import monocle.syntax.lens._
  import monocle.syntax.traversal._
  import scala.language.postfixOps

  val address2Latitude = address oo location oo latitude

  println(p >- address oo city get)
  println(p >- address oo location oo latitude modify (_ + 1))

  println(p >-- address oo location oo locationTraversal toListOf)
  println(p >-- address oo location oo locationTraversal set 2L)

}




