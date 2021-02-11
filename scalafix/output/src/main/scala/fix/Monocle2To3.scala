package fix
import monocle.Lens
import monocle.Optional
object Monocle2To3 {
  case class Address(streetNumber: Int, streetName: String)
  val streetNumber: Lens[Address, Int] =
    Lens[Address, Int](_.streetNumber)(n => a => a.copy(streetNumber = n))
  streetNumber.replace(770)

  val head = Optional[List[Int], Int] {
    case Nil     => None
    case x :: xs => Some(x)
  } { a =>
    {
      case Nil     => Nil
      case x :: xs => a :: xs
    }
  }
  head.replaceOption(1)
}
