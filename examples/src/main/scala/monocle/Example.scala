package monocle

import monocle.Macro._
import monocle.std.tuple._
import scala.Some
import scalaz.std.AllInstances._

object Example extends App {
  case class Address(_city: String, _postcode: String, _location: (Int, Int))
  case class Person(_age: Int, _name: String, _address: Address)

  // Some boiler plate code to create Lens. We can probably remove it with Macro annotation
  val postcode = mkLens[Address, String]("_postcode")
  val city     = mkLens[Address, String]("_city")
  val location = mkLens[Address, (Int, Int)]("_location")

  val age      = mkLens[Person, Int]("_age")
  val name     = mkLens[Person, String]("_name")
  val address  = mkLens[Person, Address]("_address")


  val l = (2, 6)
  val a = Address("London", "EC1...", l)
  val p = Person(25, "Roger", a)

  //basic scala
  println(p.copy(_address = p._address.copy(_city = "Paris")))

  println((address compose city).set(p, "Paris"))
  println((address compose city).get(p))
  println((address compose city).modify(p, _ + "!!!"))
  println((address compose city).lift(p, city => Option(city)))

  println(both.toListOf(l))
  println(both.set(l, 1.0))
  println(both.fold(l))
  println(both.modify(l, (_: Int) + 2))

  println(both.multiLift(l, { pos: Int => List(pos + 1, pos, pos - 1)} ))

  // composition of lenses and traversal
  println((address compose location compose both[Int, Int]).toListOf(p))

  val int2DoubleOption = Traversal[Option, Int, Double]

  val someInt: Option[Int] = Some(1)
  println(int2DoubleOption.modify(someInt, _ + 2.00))
  println(int2DoubleOption.toListOf(someInt))

  import monocle.syntax.lens._
  import scala.language.postfixOps


  println(p >- address oo city get)
  println(p >- (address oo location oo _1) modify (_ + 1))

  println(p >- address oo location oo both[Int, Int] toListOf)
  println(p >- address oo location oo both[Int, Int] set 2)

}

