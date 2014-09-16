package monocle

import monocle.syntax._
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

@State(Scope.Benchmark)
class LensBench {
  @Lenses case class Person(name: String, age: Int)

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

  @Benchmark def directGet()                = john.name                               == "John"
  @Benchmark def SimpleLensVerboseGet()     = (john |-> SimpleLensVerbose._name get)  == "John"
  @Benchmark def SimpleLensInferredGet()    = (john |-> SimpleLensInferred._name get) == "John"
  @Benchmark def MkLensMacroGet()           = (john |-> MkLensMacro.name get)         == "John"
  @Benchmark def LenserMacroGet()           = (john |-> LenserMacro.name get)         == "John"
  @Benchmark def LensesAnnotationMacroGet() = (john |-> Person.name get)              == "John"
}
