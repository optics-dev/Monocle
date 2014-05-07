package monocle

import monocle.Macro._
import monocle.syntax.lens._
import org.specs2.scalaz.Spec

class LensExample extends Spec {

  case class Location(x: Int, y: Int)
  case class Character(name: String, hp: Int, location: Location)

  // Some boiler plate code to create Lens. We should be able to remove it with Macro annotation
  val name     = mkLens[Character, String]("name")
  val hp       = mkLens[Character, Int]("hp")
  val location = mkLens[Character, Location]("location")

  val x        = mkLens[Location, Int]("x")
  val y        = mkLens[Location, Int]("y")

  val krom = Character("Krom", 30, Location(4,0))

  "Lens get extract an A from an S" in {
    (krom |-> name get)           shouldEqual "Krom"
    (krom |-> location |-> x get) shouldEqual 4
  }

  "Lens set and modify update an A in a S" in {
    (krom |-> hp set 45)                    shouldEqual Character("Krom", 45, Location(4,0))
    (krom |-> location |-> x modify(_ + 1)) shouldEqual Character("Krom", 30, Location(5,0))
  }

  "Lens lift modifies an A with a Functor and wraps the context back to S" in {
    def neighbouringBlocks(n: Int): List[Int] = List(n - 1, n, n + 1).filter(_ >= 0)

    // we need to provide an instance of Functor for List
    import scalaz.std.list._

    (krom |-> location |-> y lift neighbouringBlocks) shouldEqual List(
      Character("Krom", 30, Location(4,0)), Character("Krom", 30, Location(4,1))
    )
  }

}