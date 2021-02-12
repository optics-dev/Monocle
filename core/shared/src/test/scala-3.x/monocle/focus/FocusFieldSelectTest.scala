package monocle.focus

import monocle.Focus

final class FocusFieldSelectTest extends munit.FunSuite {

  case class Fub(bab: Int)
  case class Bar(fub: Fub)
  case class Foo(bar: Option[Bar])
  case class Qux(foo: Either[String, Foo], moo: Map[Int, Fub])

  case class Animal(name: String)
  case class Owner(pet: Animal)
  case class Shop(owner: Owner)
  case class Box[A](a: A) 
  case class MultiBox[A,B](a: A, b: B)
  case class HigherBox[F[_], A](fa: F[A])
  trait RefinedBox { type A; def a: A }
  case class UnionBox[A,B](aOrB: A | B)
  case class ConstraintBox[A <: AnyVal](a: A)
  case class Varargs[A](a: A*)

  test("Single field access") {
    assertEquals(
      Focus[Animal](_.name).get(Animal("Bob")),
      "Bob"
    )
  }

  test("Nested field access") {
    assertEquals(
      Focus[Shop](_.owner.pet.name).get(Shop(Owner(Animal("Fred")))),
      "Fred"
    )
  }

  test("Type parameter field access") {
    assertEquals(
      Focus[Box[String]](_.a).get(Box("Hello")),
      "Hello"
    )
  }

  test("Type parameter set field") {
    assertEquals(
      Focus[Box[Int]](_.a).replace(111)(Box(222)),
      Box(111)
    )
  }

  test("Nested type parameter set field") {
    assertEquals(
      Focus[Box[Box[String]]](_.a.a).replace("hello")(Box(Box("ok"))),
      Box(Box("hello"))
    )
  }

  test("Multiple type parameters get field") {
    assertEquals(
      Focus[MultiBox[Int, Boolean]](_.b).get(MultiBox(222, true)),
      true
    )
  }

  test("Multiple type parameters set field") {
    assertEquals(
      Focus[MultiBox[String, Int]](_.a).replace("abc")(MultiBox("whatevs",222)),
      MultiBox("abc", 222)
    )
  }

  test("Higher kinded type parameter get field") {
    assertEquals(
      Focus[HigherBox[Option, Int]](_.fa).get(HigherBox(Some(23))),
      Some(23)
    )
  }

  /*
  test("Refined type field accessss") {
    assertEquals(
      Focus[RefinedBox { type A = String }](_.a).get(new RefinedBox { type A = String; def a = "Bob" }),
      "Bob"
    )
  }*/

  /*
  test("Existential type field accessss") {
    val existentialBox: Box[_] = Box("abc")
    assertEquals(
      Focus[Box[_]](_.a).get(existentialBox),
      "abc"
    )
  }*/
}
