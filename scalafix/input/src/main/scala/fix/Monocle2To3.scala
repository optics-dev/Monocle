/*
rule = Monocle
 */
package fix
import monocle.Lens
import monocle.Optional
import monocle.function.all._

object Monocle2To3 {
  case class Address(streetNumber: Int, streetName: String)
  val streetNumber: Lens[Address, Int] =
    Lens[Address, Int](_.streetNumber)(n => a => a.copy(streetNumber = n))
  streetNumber.set(770)

  val head = Optional[List[Int], Int] {
    case Nil     => None
    case x :: xs => Some(x)
  } { a =>
    {
      case Nil     => Nil
      case x :: xs => a :: xs
    }
  }
  head.setOption(1)

  case class Person(name: String, address: Address, tags: Map[String, String])
  val tags: Lens[Person, Map[String, String]] = ???
  val tagsA = tags composeLens at("A")
}
