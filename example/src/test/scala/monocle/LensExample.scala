package monocle

import monocle.macros.{GenLens, Lenses}
import monocle.syntax._
import org.specs2.execute.AnyValueAsResult
import org.specs2.scalaz.Spec
import shapeless.test.illTyped

class LensExample extends Spec {
  
  @Lenses // this annotation generate lenses in the companion object of Person
  case class Person(name: String, age: Int)

  object CoreSimpleLens {
    val _name = Lens((_: Person).name)(n => c => c.copy(name = n))
    val _age  = Lens((_: Person).age)( h => c => c.copy(age = h))
  }

  object LenserMacro {
    val genLens = GenLens[Person]

    val name = genLens(_.name)
    val age  = genLens(_.age)
  }

  val john = Person("John", 30)
  
  "Lens get extract an A from an S" in {
    (john applyLens CoreSimpleLens._name get) ==== "John"
    (john applyLens LenserMacro.name get)     ==== "John"
    (john applyLens Person.name get)          ==== "John"
  }

  "Lens set and modify update an A in a S" in {
    val changedJohn = Person("John", 45)

    (john applyLens CoreSimpleLens._age set 45) ==== changedJohn
    (john applyLens LenserMacro.age set 45)     ==== changedJohn
    (john applyLens Person.age set 45)          ==== changedJohn
  }

  @Lenses("_") // this generates lenses prefixed with _ in the Cat companion object
  case class Cat(age: Int)

  val alpha = Cat(2)

  "@Lenses takes an optional prefix string" in {
    (alpha applyLens Cat._age get)   ==== 2
    (alpha applyLens Cat._age set 3) ==== Cat(3)
  }

  "Modifications through lenses are chainable" in {
    @Lenses case class Point(x: Int, y: Int)
    import Point._

    val update = x.modify(_ + 100) compose y.set(7)
    update(Point(1,2)) ==== Point(101,7)
  }

  "@Lenses is for case classes only" in {
    new AnyValueAsResult[Unit].asResult(
      illTyped("""@Lenses class C""", "Invalid annotation target: must be a case class")
    )
  }
}
