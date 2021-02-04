package fix
import monocle.Lens

object Monocle2To3 {
  case class Address(streetNumber: Int, streetName: String)
  val streetNumber: Lens[Address, Int] =
    Lens[Address, Int](_.streetNumber)(n => a => a.copy(streetNumber = n))

  streetNumber.replace(770)
}
