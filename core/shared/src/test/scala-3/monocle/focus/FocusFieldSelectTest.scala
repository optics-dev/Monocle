package monocle.focus

import monocle.{Focus, Getter, Iso, Lens}

final class FocusFieldSelectTest extends munit.FunSuite {

  case class Fub(bab: Int)
  case class Bar(fub: Fub)
  case class Foo(bar: Option[Bar])
  case class Qux(foo: Either[String, Foo], moo: Map[Int, Fub])

  case class Animal(name: String)
  case class Owner(pet: Animal)
  case class Shop(owner: Owner)
  case class Box[A](a: A)
  case class MultiBox[A, B](a: A, b: B)
  case class HigherBox[F[_], A](fa: F[A])
  trait RefinedBox { type A; def a: A }
  case class UnionBox[A, B](aOrB: A | B)
  case class ConstraintBox[A <: AnyVal](a: A)
  case class Varargs[A](a: A*)
  opaque type OpaqueType = Fub
  final class NonCaseClass(val value: Owner)

  object Public {
    case class Private(private[Public] a: Int)
    def lens: Lens[Private, Int] = Focus[Private](_.a)
  }

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
      Focus[MultiBox[String, Int]](_.a).replace("abc")(MultiBox("whatevs", 222)),
      MultiBox("abc", 222)
    )
  }

  test("Higher kinded type parameter get field") {
    assertEquals(
      Focus[HigherBox[Option, Int]](_.fa).get(HigherBox(Some(23))),
      Some(23)
    )
  }

  test("Private field access") {
    val a = Public.Private(44)

    assertEquals(Public.lens.get(a), 44)
    assertEquals(Public.lens.replace(55)(a), Public.Private(55))
  }

  test("Tuple field access") {
    val f1 = Focus[(Int, String)](_._1)
    val f2 = Focus[(Int, String)](_._2)

    val tup = (11, "a")

    assertEquals(f1.get(tup), 11)
    assertEquals(f2.get(tup), "a")
  }

  test("Tuple set field") {
    val f1 = Focus[(Int, String)](_._1)
    val f2 = Focus[(Int, String)](_._2)

    val tup = (11, "a")

    assertEquals(f1.replace(99)(tup), (99, "a"))
    assertEquals(f2.replace("moo")(tup), (11, "moo"))
  }

  test("Single field should be an Iso") {
    val iso: Iso[Animal, String] = Focus[Animal](_.name)
    assertEquals(iso.get(Animal("Bob")), "Bob")
    assertEquals(iso.reverseGet("Bob"), Animal("Bob"))
  }

  test("Type alias for parameterised type access") {
    case class CC[T](t: T, i: Int)
    type CCInt = CC[Int]
    val cc = CC(2, 3)

    assertEquals(Focus[CCInt](_.i).get(cc), 3)
    assertEquals(Focus[CCInt](_.t).get(cc), 2)
  }

  test("Opaque type field access") {
    val lens: Lens[OpaqueType, Int] = Focus[OpaqueType](_.bab)
    assertEquals(
      lens.get(Fub(1)),
      1
    )
  }

  test("Non case-class field access") {
    val getter: Getter[NonCaseClass, String] = Focus[NonCaseClass](_.value.pet.name)
    assertEquals(
      getter.get(NonCaseClass(Owner(Animal("fido")))),
      "fido"
    )
  }

  /*
  test("Refined type field access") {
    assertEquals(
      Focus[RefinedBox { type A = String }](_.a).get(new RefinedBox { type A = String; def a = "Bob" }),
      "Bob"
    )
  }*/

  /*
  test("Existential type field access") {
    val existentialBox: Box[_] = Box("abc")
    assertEquals(
      Focus[Box[_]](_.a).get(existentialBox),
      "abc"
    )
  }*/
}
