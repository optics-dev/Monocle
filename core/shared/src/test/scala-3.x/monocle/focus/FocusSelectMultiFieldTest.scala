package monocle.focus

import monocle.{Focus, Lens}

final class FocusSelectMultiFieldTest extends munit.FunSuite {

  sealed trait Item { def price: Int }
  case class Hammer(weight: Int, price: Int) extends Item
  case class Shovel(price: Int, isSharp: Boolean) extends Item

  sealed trait OneOrTwo[+A] { def a: A; def doco: String }
  case class One[A](a: A, doco: String) extends OneOrTwo[A]
  case class Two[X](a: X, b: X, doco: String) extends OneOrTwo[X]

  test("Get shared field from case") {
    assertEquals(Focus[Item](_.price).get(Hammer(33, 10)), 10)
  }

  test("Set shared field from case") {
    assertEquals(Focus[Item](_.price).replace(99)(Shovel(88, true)), Shovel(99, true))
  }

  test("Get/set shared type argument field from a parameterised trait") {
    val lens: Lens[OneOrTwo[Int], Int] = Focus[OneOrTwo[Int]](_.a)
    val oneOrTwo: OneOrTwo[Int] = One(5, "It is five")

    assertEquals(lens.get(oneOrTwo), 5)
    assertEquals(lens.replace(50)(oneOrTwo), One(50, "It is five"))
  }

  test("Get/set shared field from a parameterised trait") {
    val lens: Lens[OneOrTwo[Int], String] = Focus[OneOrTwo[Int]](_.doco)
    val oneOrTwo: OneOrTwo[Int] = Two(999, 99, "Almost there")

    assertEquals(lens.get(oneOrTwo), "Almost there")
    assertEquals(lens.replace("All nines")(oneOrTwo), Two(999, 99, "All nines"))
  }
}
