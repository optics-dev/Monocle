
import lens.Macro

object MacroExample extends App {

  case class Person(name: String)

  val person = Person("Paul")

  val name = Macro.mkGetter[Person, String]("name")

  println("Name: " + name(person))



}
