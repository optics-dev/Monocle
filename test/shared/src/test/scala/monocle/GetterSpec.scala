package monocle

import cats.Semigroupal
import cats.arrow.{Arrow, Category, Choice, Compose, Profunctor}

class GetterSpec extends MonocleSuite {
  case class Bar(i: Int)
  case class Foo(bar: Bar)

  val bar = Getter[Foo, Bar](_.bar)
  val i   = Getter[Bar, Int](_.i)

  // test implicit resolution of type classes

  test("Getter has a Compose instance") {
    assertEquals(Compose[Getter].compose(i, bar).get(Foo(Bar(3))), 3)
  }

  test("Getter has a Category instance") {
    assertEquals(Category[Getter].id[Int].get(3), 3)
  }

  test("Getter has a Choice instance") {
    assertEquals(
      Choice[Getter]
        .choice(i, Choice[Getter].id[Int])
        .get(Left(Bar(3))),
      3
    )
  }

  test("Getter has a Profunctor instance") {
    assertEquals(Profunctor[Getter].rmap(bar)(_.i).get(Foo(Bar(3))), 3)
  }

  test("Getter has a Arrow instance") {
    assertEquals(Arrow[Getter].lift((_: Int) * 2).get(4), 8)
  }

  test("Getter has a Semigroupal instance") {
    val length = Getter[String, Int](_.length)
    val upper  = Getter[String, String](_.toUpperCase)
    assertEquals(
      Semigroupal[Getter[String, *]]
        .product(length, upper)
        .get("helloworld"),
      ((10, "HELLOWORLD"))
    )
  }

  test("get") {
    assertEquals(i.get(Bar(5)), 5)
  }

  test("find") {
    assertEquals(i.find(_ > 5)(Bar(9)), Some(9))
    assertEquals(i.find(_ > 5)(Bar(3)), None)
  }

  test("exist") {
    assertEquals(i.exist(_ > 5)(Bar(9)), true)
    assertEquals(i.exist(_ > 5)(Bar(3)), false)
  }

  test("zip") {
    val length = Getter[String, Int](_.length)
    val upper  = Getter[String, String](_.toUpperCase)
    assertEquals(length.zip(upper).get("helloworld"), ((10, "HELLOWORLD")))
  }

  test("to") {
    assertEquals(i.to(_.toString()).get(Bar(5)), "5")
  }

  test("some") {
    case class SomeTest(x: Int, y: Option[Int])
    val obj = SomeTest(1, Some(2))

    val getter = Getter((_: SomeTest).y)

    assertEquals(getter.some.getAll(obj), List(2))
    assertEquals(obj.applyGetter(getter).some.getAll, List(2))
  }

  test("withDefault") {
    case class SomeTest(x: Int, y: Option[Int])
    val objSome = SomeTest(1, Some(2))
    val objNone = SomeTest(1, None)

    val getter = Getter((_: SomeTest).y)

    assertEquals(getter.withDefault(0).get(objSome), 2)
    assertEquals(getter.withDefault(0).get(objNone), 0)

    assertEquals(objSome.applyGetter(getter).withDefault(0).get, 2)
    assertEquals(objNone.applyGetter(getter).withDefault(0).get, 0)
  }

  test("each") {
    case class SomeTest(x: Int, y: List[Int])
    val obj = SomeTest(1, List(1, 2, 3))

    val getter = Getter((_: SomeTest).y)

    assertEquals(getter.each.getAll(obj), List(1, 2, 3))
    assertEquals(obj.applyGetter(getter).each.getAll, List(1, 2, 3))
  }
}
