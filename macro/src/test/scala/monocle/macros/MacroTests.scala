package monocle.macros

import probably._

case class Street(number: Int, name: String)
case class Address(city: String, street: Street)
case class Company(name: String, address: Address)
case class Employee(name: String, company: Company)

sealed trait Json
case class JNum(num: Double) extends Json

object MacroTests extends Suite("Monocle Macro Tests") {
  def run(test: Runner): Unit = {
    test("construct Lens[Employee, Address] using GenLens") {
      GenLens[Employee](_.company.address)
    }.assert(_ => true)

    test("construct Lens[Employee, Int] using GenLens") {
      GenLens[Employee](_.company.address.street.number)
    }.assert(_ => true)

    test("construct Iso[JNum, Double] using GenIso") {
      GenIso[JNum, Double]
    }.assert(_ => true)

    test("construct Prism[Json, JNum] using GenPrism ") {
      GenPrism[Json, JNum]
    }.assert(_ => true)
  }
}
