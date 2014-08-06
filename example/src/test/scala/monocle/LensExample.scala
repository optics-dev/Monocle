package monocle

import monocle.syntax._
import org.specs2.scalaz.Spec

class LensExample extends Spec {
  @Lenses case class Location(x: Int, y: Int)
  @Lenses case class Character(name: String, hp: Int, location: Location)
  import Location._
  import Character._

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

  "Modifications through lenses are chainable" in {
    val m = x.modifyF(_ + 100) compose y.setF(7)
    m(Location(1,2)) shouldEqual Location(101,7)
  }

}
