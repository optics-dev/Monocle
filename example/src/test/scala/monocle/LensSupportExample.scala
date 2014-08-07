package monocle

import monocle.syntax._
import org.specs2.scalaz.Spec

class LensSupportExample extends Spec {
  case class Location(x: Int, y: Int)
  case class Character(name: String, hp: Int, location: Location)

  val krom = Character("Krom", 30, Location(4,0))

  object Location {
    val lenser = Lenser[Location]
    val x = lenser(_.x)
    val y = lenser(_.y)
  }

  object Character {
    val lenser = Lenser[Character]
    val name = lenser(_.name)
    val hp = lenser(_.hp)
    val location = lenser(_.location)
  }
  
  import Location._
  import Character._

  "Lens get extract an A from an S" in {
    (krom |-> name get)           shouldEqual "Krom"
    (krom |-> location |-> x get) shouldEqual 4
  }

  "Lens set and modify update an A in a S" in {
    (krom |-> hp set 45)                    shouldEqual Character("Krom", 45, Location(4,0))
    (krom |-> location |-> x modify(_ + 1)) shouldEqual Character("Krom", 30, Location(5,0))
  }

  "Modifications through lenses are chainable" in {
    val m = x.modifyF(_ + 100) compose y.setF(7)
    m(Location(1,2)) shouldEqual Location(101,7)
  }

}
