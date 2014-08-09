package monocle

import monocle.syntax._
import org.specs2.execute.AnyValueAsResult
import org.specs2.scalaz.Spec
import shapeless.test.illTyped

class LensExample extends Spec {
  
  @Lenses // this annotation generate lenses in the companion object of Person
  case class Person(name: String, age: Int)

  object SimpleLensVerbose {
    val _name = SimpleLens[Person, String](_.name, (c, n) => c.copy(name = n))
    val _age  = SimpleLens[Person, Int](_.age, (c, h) => c.copy(age = h))
  }

  object SimpleLensInferred {
    val _name = SimpleLens[Person](_.name)((c, n) => c.copy(name = n))
    val _age  = SimpleLens[Person](_.age)((c, h) => c.copy(age = h))
  }

  object MkLensMacro {
    import monocle.Macro._

    val name = mkLens[Person, String]("name")
    val age  = mkLens[Person, Int]("age")
  }

  object LenserMacro {
    val lenser = Lenser[Person]

    val name = lenser(_.name)
    val age  = lenser(_.age)
  }

  val john = Person("John", 30)
  
  "Lens get extract an A from an S" in {
    (john  |-> SimpleLensVerbose._name get)   shouldEqual "John"
    (john  |-> SimpleLensInferred._name get)  shouldEqual "John"
    (john  |-> MkLensMacro.name get)          shouldEqual "John"
    (john  |-> LenserMacro.name get)          shouldEqual "John"
    (john  |-> Person.name get)               shouldEqual "John"
  }

  "Lens set and modify update an A in a S" in {
    val changedJohn = Person("John", 45)

    (john  |-> SimpleLensVerbose._age set 45)  shouldEqual changedJohn
    (john  |-> SimpleLensInferred._age set 45) shouldEqual changedJohn
    (john  |-> MkLensMacro.age set 45)         shouldEqual changedJohn
    (john  |-> LenserMacro.age set 45)         shouldEqual changedJohn
    (john  |-> Person.age set 45)              shouldEqual changedJohn
  }

  "Modifications through lenses are chainable" in {
    @Lenses case class Point(x: Int, y: Int)
    import Point._

    val update = x.modifyF(_ + 100) compose y.setF(7)
    update(Point(1,2)) shouldEqual Point(101,7)
  }

  "@Lenses is for case classes only" in {
    new AnyValueAsResult[Unit].asResult(
      illTyped("""@Lenses class C""", "Invalid annotation target: must be a case class")
    )
  }
}
