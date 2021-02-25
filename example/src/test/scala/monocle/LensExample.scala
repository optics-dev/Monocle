package monocle

import monocle.macros.{GenLens, Lenses, PLenses}
import shapeless.test.illTyped

class LensMonoExample extends MonocleSuite {
  // @Lenses generate Lens automatically in the companion object
  @Lenses case class Address(streetNumber: Int, streetName: String)
  @Lenses case class Person(name: String, age: Int, address: Address)

  object Manual { // Lens created manually (i.e. without macro)
    val _name         = Lens[Person, String](_.name)(n => p => p.copy(name = n))
    val _age          = Lens[Person, Int](_.age)(a => p => p.copy(age = a))
    val _address      = Lens[Person, Address](_.address)(a => p => p.copy(address = a))
    val _streetNumber = Lens[Address, Int](_.streetNumber)(n => a => a.copy(streetNumber = n))
  }

  object Semi { // Lens generated semi automatically using GenLens macro
    val name         = GenLens[Person](_.name)
    val age          = GenLens[Person](_.age)
    val address      = GenLens[Person](_.address)
    val streetNumber = GenLens[Address](_.streetNumber)
  }

  val john = Person("John", 30, Address(126, "High Street"))

  test("get") {
    assertEquals(Manual._name.get(john), "John")
    assertEquals(Semi.name.get(john), "John")
    assertEquals(Person.name.get(john), "John")
    assertEquals(john.focus(_.name).get, "John")
  }

  test("set") {
    val changedJohn = john.copy(age = 45)

    assertEquals(Manual._age.replace(45)(john), changedJohn)
    assertEquals(Semi.age.replace(45)(john), changedJohn)
    assertEquals(Person.age.replace(45)(john), changedJohn)
    assertEquals(john.focus(_.age).replace(45), changedJohn)
  }

  test("compose") {
    assertEquals((Manual._address andThen Manual._streetNumber).get(john), 126)
    assertEquals((Semi.address andThen Semi.streetNumber).get(john), 126)
    assertEquals((Person.address andThen Address.streetNumber).get(john), 126)
    assertEquals(john.focus(_.address.streetNumber).get, 126)
  }

  @Lenses("_") // this generates lenses prefixed with _ in the Cat companion object
  case class Cat(age: Int)

  val alpha = Cat(2)

  test("@Lenses takes an optional prefix string") {
    assertEquals(Cat._age.get(alpha), 2)
  }

  test("Modifications through lenses are chainable") {
    @Lenses case class Point(x: Int, y: Int)
    import Point._

    val update = x.modify(_ + 100) compose y.replace(7)
    assertEquals(update(Point(1, 2)), Point(101, 7))
  }

  test("@Lenses is for case classes only") {
    illTyped("""@Lenses class C""", "Invalid annotation target: must be a case class")
  }

  test("GenApplyLensOps has no collision with .value") {
    case class MyString(s: String)
    object MyString {
      implicit class Ops(self: MyString) {
        val value: String = self.s
      }
    }
    assertEquals(MyString("a").value, "a")
  }
}

class LensPolyExample extends MonocleSuite {
  @PLenses case class Foo[A, B](q: Map[(A, B), Double], default: Double)

  object Manual { // Lens created manually (i.e. without macro)
    def q[A, B]       = Lens((_: Foo[A, B]).q)(q => f => f.copy(q = q))
    def default[A, B] = Lens((_: Foo[A, B]).default)(d => f => f.copy(default = d))
  }

  object Semi { // Lens generated semi automatically using GenLens macro
    def q[A, B]       = GenLens[Foo[A, B]](_.q)
    def default[A, B] = GenLens[Foo[A, B]](_.default)
  }

  val candyTrade = Foo(Map[(Int, Symbol), Double]((0, Symbol("Buy")) -> -3.0, (12, Symbol("Sell")) -> 7), 0.0)

  test("get") {
    assertEquals(Manual.default.get(candyTrade), 0.0)
    assertEquals(Semi.default.get(candyTrade), 0.0)
    assertEquals(Foo.default.get(candyTrade), 0.0)
  }

  test("set") {
    val changedTrade = candyTrade.copy(q = candyTrade.q.updated((0, Symbol("Buy")), -2.0))
    assertEquals(
      Foo.q.modify((_: Map[(Int, Symbol), Double]).updated((0, Symbol("Buy")), -2.0))(candyTrade),
      changedTrade
    )
  }

  test("@PLenses generates polymorphic lenses") {
    val changedTrade = candyTrade.copy(q = candyTrade.q.map { case (x, y) => (x.swap, y) })
    assertEquals(
      Foo.q
        .modify((_: Map[(Int, Symbol), Double]).map { case (x, y) => (x.swap, y) })(candyTrade),
      changedTrade
    )
  }

  @PLenses("_") case class Cat(age: Int)

  val alpha = Cat(2)

  test("@PLenses takes an optional prefix string") {
    assertEquals(Cat._age.get(alpha), 2)
  }
}
