package monocle

import monocle.macros.{GenLens, Lenses}
import shapeless.test.illTyped

class LensMonoExample extends MonocleSuite {

  // @Lenses generate Lens automatically in the companion object
  @Lenses case class Address(streetNumber: Int, streetName: String)
  @Lenses case class Person(name: String, age: Int, address: Address)

  object Manual { // Lens created manually (i.e. without macro)
  val _name = Lens[Person, String](_.name)(n => p => p.copy(name = n))
    val _age  = Lens[Person, Int](_.age)(a => p => p.copy(age = a))
    val _address = Lens[Person, Address](_.address)(a => p => p.copy(address = a))
    val _streetNumber = Lens[Address, Int](_.streetNumber)(n => a => a.copy(streetNumber = n))
  }

  object Semi { // Lens generated semi automatically using GenLens macro
  val name = GenLens[Person](_.name)
    val age  = GenLens[Person](_.age)
    val address = GenLens[Person](_.address)
    val streetNumber = GenLens[Address](_.streetNumber)
  }

  val john = Person("John", 30, Address(126, "High Street"))

  test("get") {
    Manual._name.get(john) shouldEqual "John"
    Semi.name.get(john)    shouldEqual "John"
    Person.name.get(john)  shouldEqual "John"
  }

  test("set") {
    val changedJohn = john.copy(age = 45)

    Manual._age.set(45)(john) shouldEqual changedJohn
    Semi.age.set(45)(john)    shouldEqual changedJohn
    Person.age.set(45)(john)  shouldEqual changedJohn
  }

  test("compose") {
    (Manual._address composeLens Manual._streetNumber).get(john) shouldEqual 126
    (Semi.address composeLens Semi.streetNumber).get(john)       shouldEqual 126
    (Person.address composeLens Address.streetNumber).get(john)  shouldEqual 126
  }

  @Lenses("_") // this generates lenses prefixed with _ in the Cat companion object
  case class Cat(age: Int)

  val alpha = Cat(2)

  test("@Lenses takes an optional prefix string") {
    Cat._age.get(alpha) shouldEqual 2
  }

  test("Modifications through lenses are chainable") {
    @Lenses case class Point(x: Int, y: Int)
    import Point._

    val update = x.modify(_ + 100) compose y.set(7)
    update(Point(1,2)) shouldEqual Point(101,7)
  }

  test("@Lenses is for case classes only") {
    illTyped( """@Lenses class C""", "Invalid annotation target: must be a case class")
  }
}

class LensPolyExample extends MonocleSuite {

  @Lenses case class Foo[A,B](q: Map[(A,B),Double], default: Double)

  object Manual { // Lens created manually (i.e. without macro)
  def q[A,B] = Lens((_: Foo[A,B]).q)(q => f => f.copy(q = q))
    def default[A,B] = Lens((_: Foo[A,B]).default)(d => f => f.copy(default = d))
  }

  object Semi { // Lens generated semi automatically using GenLens macro
  def q[A,B] = GenLens[Foo[A,B]](_.q)
    def default[A,B] = GenLens[Foo[A,B]](_.default)
  }

  val candyTrade = Foo(Map[(Int,Symbol),Double]((0,'Buy) -> -3.0, (12,'Sell) -> 7), 0.0)

  test("get") {
    Manual.default.get(candyTrade) shouldEqual 0.0
    Semi.default.get(candyTrade)   shouldEqual 0.0
    Foo.default.get(candyTrade)    shouldEqual 0.0
  }

  test("set") {
    val changedTrade = candyTrade.copy(q = candyTrade.q.updated((0,'Buy), -2.0))
    Foo.q.modify((_: Map[(Int,Symbol),Double]).updated((0,'Buy), -2.0))(candyTrade) shouldEqual changedTrade
  }

}
