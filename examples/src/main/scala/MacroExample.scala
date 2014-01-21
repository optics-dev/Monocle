
import lens.Macro

object MacroExample extends App {

  case class Person(name: String)

  val person = Person("Paul")

  val name = Macro.get[Person, String](person, "name")

  println("Name: " + name)



}
