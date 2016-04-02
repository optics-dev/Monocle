package monocle

import scalaz._

class GetterSpec extends MonocleSuite {

  case class Bar(i: Int)
  case class Foo(bar: Bar)

  val _bar = Getter[Foo, Bar](_.bar)
  val _i   = Getter[Bar, Int](_.i)


  // test implicit resolution of type classes

  test("Getter has a Compose)stance") {
    Compose[Getter].compose(_i, _bar).get(Foo(Bar(3))) shouldEqual 3
  }

  test("Getter has a Category)stance") {
    Category[Getter].id[Int].get(3) shouldEqual 3
  }

  test("Getter has a Choice)stance") {
    Choice[Getter].choice(_i, Choice[Getter].id[Int]).get(-\/(Bar(3))) shouldEqual 3
  }

  test("Getter has a Split)stance") {
    Split[Getter].split(_i, _bar).get((Bar(3), Foo(Bar(3)))) shouldEqual ((3, Bar(3)))
  }

  test("Getter has a Profunctor)stance") {
    Profunctor[Getter].mapsnd(_bar)(_.i).get(Foo(Bar(3))) shouldEqual 3
  }

  test("Getter has a Arrow instance") {
    Arrow[Getter].arr((_: Int) * 2).get(4) shouldEqual 8
  }

}
