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
    Compose[Getter].compose(i, bar).get(Foo(Bar(3))) shouldEqual 3
  }

  test("Getter has a Category instance") {
    Category[Getter].id[Int].get(3) shouldEqual 3
  }

  test("Getter has a Choice instance") {
    Choice[Getter].choice(i, Choice[Getter].id[Int]).get(Left(Bar(3))) shouldEqual 3
  }

  test("Getter has a Profunctor instance") {
    Profunctor[Getter].rmap(bar)(_.i).get(Foo(Bar(3))) shouldEqual 3
  }

  test("Getter has a Arrow instance") {
    Arrow[Getter].lift((_: Int) * 2).get(4) shouldEqual 8
  }

  test("Getter has a Semigroupal instance") {
    val length = Getter[String, Int](_.length)
    val upper  = Getter[String, String](_.toUpperCase)
    Semigroupal[Getter[String, *]].product(length, upper).get("helloworld") shouldEqual ((10, "HELLOWORLD"))
  }

  test("get") {
    i.get(Bar(5)) shouldEqual 5
  }

  test("find") {
    i.find(_ > 5)(Bar(9)) shouldEqual Some(9)
    i.find(_ > 5)(Bar(3)) shouldEqual None
  }

  test("exist") {
    i.exist(_ > 5)(Bar(9)) shouldEqual true
    i.exist(_ > 5)(Bar(3)) shouldEqual false
  }

  test("zip") {
    val length = Getter[String, Int](_.length)
    val upper  = Getter[String, String](_.toUpperCase)
    length.zip(upper).get("helloworld") shouldEqual ((10, "HELLOWORLD"))
  }
}
