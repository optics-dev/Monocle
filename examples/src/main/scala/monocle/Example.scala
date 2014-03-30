package monocle

import monocle.Macro._
import monocle.std.tuple._
import scala.Some
import scalaz.std.AllInstances._

object Example extends App {

  case class Address(_city: String, _postcode: String, _location: (Int, Int))
  case class Person(_age: Int, _name: String, _address: Address)

  // Some boiler plate code to create Lens. We should be able to remove it with Macro annotation
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
  println( p.copy(_address = p._address.copy(_city = "Paris")) )

  println( (address compose city).get(p) ) // "Paris"
  println( (address compose city).set(p, "Paris") )      // Person(25, "Roger", Address("Paris"    , "EC1...", (2, 6)))
  println( (address compose city).modify(p, _ + "!!!") ) // Person(25, "Roger", Address("London!!!", "EC1...", (2, 6)))
  println( (address compose city).lift(p, city => Option(city)) ) // Some(Person(25, "Roger", Address("London", "EC1...", (2, 6))))

  println( both.getAll(l) )   // List(2, 6)
  println( both.set(l, 1.0) ) // (1.0, 1.0)
  println( both.fold(l) )     // 8
  println( both.modify(l, (_: Int) + 2) ) // (4, 8)

  println( both.multiLift(l, { pos: Int => List(pos + 1, pos, pos - 1)} ) ) // List((3,7), (3,6), (3,5), (2,7), (2,6), (2,5), (1,7), (1,6), (1,5))

  // composition of lenses and traversal
  println( (address compose location compose both[Int, Int]).getAll(p) )

  import monocle.syntax.lens._

  println(p |-> address |-> city get)                       // London
  println(p |-> address |-> location |->> both getAll)      // List(2, 6)
  println(p |-> address |-> location |-> _1 modify (_ + 1)) // Person(25, "Roger", Address("London", "EC1...", (3, 6)))
  println(p |-> address |-> location |->> both set 2)       // Person(25, "Roger", Address("London", "EC1...", (2, 2)))

}

