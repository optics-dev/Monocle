package monocle

import org.specs2.scalaz.Spec
import monocle.syntax._

class CreateLensExample extends Spec {
  case class Character(name: String, hp: Int)

  val krom = Character("Krom", 30)

  object SimpleLensVerbose {
    val _name = SimpleLens[Character, String](_.name, (c, n) => c.copy(name = n))
    val _hp = SimpleLens[Character, Int](_.hp, (c, h) => c.copy(hp = h))
  }

  object SimpleLensInferred {
    val _name = SimpleLens[Character](_.name)((c, n) => c.copy(name = n))
    val _hp = SimpleLens[Character](_.hp)((c, h) => c.copy(hp = h))
  }

  object MkLensMacro {
    import monocle.Macro._

    val name = mkLens[Character, String]("name")
    val hp = mkLens[Character, Int]("hp")
  }

  object LenserMacro {
    val lenser = Lenser[Character]

    val name = lenser(_.name)
    val hp = lenser(_.hp)
  }

  object MacroAnnotation {
    @Lenses
    case class Character2(name: String, hp: Int)

    val krom2 = Character2("Krom", 30)
  }
  import MacroAnnotation._

  "Lens get extract an A from an S" in {
    (krom  |-> SimpleLensVerbose._name get)   shouldEqual "Krom"
    (krom  |-> SimpleLensInferred._name get)  shouldEqual "Krom"
    (krom  |-> MkLensMacro.name get)          shouldEqual "Krom"
    (krom  |-> LenserMacro.name get)          shouldEqual "Krom"
    (krom2 |-> Character2.name get)           shouldEqual "Krom"
  }

  "Lens set and modify update an A in a S" in {
    val changedKrom = Character("Krom", 45)

    (krom  |-> SimpleLensVerbose._hp set 45)  shouldEqual changedKrom
    (krom  |-> SimpleLensInferred._hp set 45) shouldEqual changedKrom
    (krom  |-> MkLensMacro.hp set 45)         shouldEqual changedKrom
    (krom  |-> LenserMacro.hp set 45)         shouldEqual changedKrom
    (krom2 |-> Character2.hp set 45)          shouldEqual Character2("Krom", 45)
  }
}
