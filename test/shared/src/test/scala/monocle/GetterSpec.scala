package monocle

import scalaz._

class GetterSpec extends MonocleSuite {

  case class Bar(i: Int)
  case class Foo(bar: Bar)

  val bar = Getter[Foo, Bar](_.bar)
  val i   = Getter[Bar, Int](_.i)


  // test implicit resolution of type classes

  test("Getter has a Compose)stance") {
    Compose[Getter].compose(i, bar).get(Foo(Bar(3))) shouldEqual 3
  }

  test("Getter has a Category)stance") {
    Category[Getter].id[Int].get(3) shouldEqual 3
  }

  test("Getter has a Choice)stance") {
    Choice[Getter].choice(i, Choice[Getter].id[Int]).get(-\/(Bar(3))) shouldEqual 3
  }

  test("Getter has a Split)stance") {
    Split[Getter].split(i, bar).get((Bar(3), Foo(Bar(3)))) shouldEqual ((3, Bar(3)))
  }

  test("Getter has a Profunctor)stance") {
    Profunctor[Getter].mapsnd(bar)(_.i).get(Foo(Bar(3))) shouldEqual 3
  }

  test("Getter has a Arrow instance") {
    Arrow[Getter].arr((_: Int) * 2).get(4) shouldEqual 8
  }

  test("Getter has a Zip instance") {
    val length = Getter[String, Int](_.length)
    val upper = Getter[String, String](_.toUpperCase)
    Zip[Getter[String, ?]].zip(length, upper).get("helloworld") shouldEqual((10, "HELLOWORLD"))
  }

  test("Getter has a Unzip instance") {
    val lengthAndUpper = Getter[String, (Int, String)](s => s.length -> s.toUpperCase)
    val (length, upper) = Unzip[Getter[String, ?]].unzip(lengthAndUpper)
    length.get("helloworld") shouldEqual 10
    upper.get("helloworld") shouldEqual "HELLOWORLD"
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
    val upper = Getter[String, String](_.toUpperCase)
    length.zip(upper).get("helloworld") shouldEqual((10, "HELLOWORLD"))
  }

  test("zip/unzip roundtrip") {
    val length = Getter[String, Int](_.length)
    val upper = Getter[String, String](_.toUpperCase)

    val (length1, upper1) = Unzip[Getter[String, ?]].unzip(length.zip(upper))
    length1.get("helloworld") shouldEqual length.get("helloworld")
    upper.get("helloworld") shouldEqual upper.get("helloworld")
  }
}
