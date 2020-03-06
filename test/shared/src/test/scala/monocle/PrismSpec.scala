package monocle

import monocle.law.discipline.{OptionalTests, PrismTests, SetterTests, TraversalTests}
import monocle.macros.GenIso
import monocle.macros.GenPrism

import cats.arrow.{Category, Compose}
import cats.syntax.either._

class PrismSpec extends MonocleSuite {
  def _right[E, A]: Prism[Either[E, A], A] = Prism[Either[E, A], A](_.toOption)(Either.right)
  def _pright[E, A]: Prism[Either[E, A], A] =
    Prism.partial[Either[E, A], A](Function.unlift(_.toOption))(Either.right)

  val _nullary: Prism[Arities, Unit] =
    Prism[Arities, Unit] {
      case Nullary() => Some(())
      case _         => None
    } {
      case () => Nullary()
    }
  val _unary: Prism[Arities, Int] =
    Prism[Arities, Int] {
      case Unary(i) => Some(i)
      case _        => None
    }(Unary)
  val _binary: Prism[Arities, (String, Int)] =
    Prism[Arities, (String, Int)] {
      case Binary(s, i) => Some((s, i))
      case _            => None
    }(Binary.tupled)
  val _quintary: Prism[Arities, (Char, Boolean, String, Int, Double)] =
    Prism[Arities, (Char, Boolean, String, Int, Double)] {
      case Quintary(c, b, s, i, f) => Some((c, b, s, i, f))
      case _                       => None
    }(Quintary.tupled)

  checkAll("apply Prism", PrismTests(_right[String, Int]))
  checkAll("apply partial Prism", PrismTests(_pright[String, Int]))

  checkAll("prism.asTraversal", OptionalTests(_right[String, Int].asOptional))
  checkAll("prism.asTraversal", TraversalTests(_right[String, Int].asTraversal))
  checkAll("prism.asSetter", SetterTests(_right[String, Int].asSetter))

  checkAll("first", PrismTests(_right[String, Int].first[Boolean]))
  checkAll("second", PrismTests(_right[String, Int].second[Boolean]))
  checkAll("left", PrismTests(_right[String, Int].left[Boolean]))
  checkAll("right", PrismTests(_right[String, Int].right[Boolean]))

  // test implicit resolution of type classes

  test("Prism has a Compose instance") {
    Compose[Prism]
      .compose(_right[String, Int], _right[String, Either[String, Int]])
      .getOption(Right(Right(3))) shouldEqual Some(3)
  }

  test("Prism has a Category instance") {
    Category[Prism].id[Int].getOption(3) shouldEqual Some(3)
  }

  test("only") {
    Prism.only(5).getOption(5) shouldEqual Some(())
  }

  test("below") {
    val _5s = Prism.only(5).below[List]
    _5s.getOption(List(1, 2, 3, 4, 5)) shouldEqual None
    _5s.getOption(List(5, 5, 5)) shouldEqual Some(List((), (), ()))
  }

  test("apply") {
    _nullary() shouldEqual Nullary()
    _unary(3) shouldEqual Unary(3)
    _binary("foo", 7) shouldEqual Binary("foo", 7)
    _quintary('x', true, "bar", 13, 0.4) shouldEqual
      Quintary('x', true, "bar", 13, 0.4)
  }

  test("unapply") {
    ((Nullary(): Arities) match { case _nullary(unit)       => unit }) shouldEqual (())
    ((Unary(3): Arities) match { case _unary(value)         => value * 2 }) shouldEqual 6
    ((Binary("foo", 7): Arities) match { case _binary(s, i) => s + i }) shouldEqual "foo7"
    ((Quintary('x', true, "bar", 13, 0.4): Arities) match {
      case _quintary(c, b, s, i, f) => "" + c + b + s + i + f
    }) shouldEqual "xtruebar130.4"
  }

  sealed trait IntOrString
  case class I(i: Int)    extends IntOrString
  case class S(s: String) extends IntOrString

  val i = GenPrism[IntOrString, I] composeIso GenIso[I, Int]
  val s = Prism[IntOrString, String] { case S(s) => Some(s); case _ => None }(S.apply)

  test("getOption") {
    i.getOption(I(1)) shouldEqual Some(1)
    i.getOption(S("")) shouldEqual None

    s.getOption(S("hello")) shouldEqual Some("hello")
    s.getOption(I(10)) shouldEqual None
  }

  test("reverseGet") {
    i.reverseGet(3) shouldEqual I(3)
    s.reverseGet("Yop") shouldEqual S("Yop")
  }

  test("isEmpty") {
    i.isEmpty(I(1)) shouldEqual false
    i.isEmpty(S("")) shouldEqual true
  }

  test("nonEmpty") {
    i.nonEmpty(I(1)) shouldEqual true
    i.nonEmpty(S("")) shouldEqual false
  }

  test("find") {
    i.find(_ > 5)(I(9)) shouldEqual Some(9)
    i.find(_ > 5)(I(2)) shouldEqual None
  }

  test("exist") {
    i.exist(_ > 5)(I(9)) shouldEqual true
    i.exist(_ > 5)(I(2)) shouldEqual false
    i.exist(_ > 5)(S("")) shouldEqual false
  }

  test("all") {
    i.all(_ > 5)(I(9)) shouldEqual true
    i.all(_ > 5)(I(2)) shouldEqual false
    i.all(_ > 5)(S("")) shouldEqual true
  }

  test("modify") {
    i.modify(_ + 1)(I(3)) shouldEqual I(4)
    i.modify(_ + 1)(S("")) shouldEqual S("")
  }

  test("modifyOption") {
    i.modifyOption(_ + 1)(I(3)) shouldEqual Some(I(4))
    i.modifyOption(_ + 1)(S("")) shouldEqual None
  }

  test("set") {
    i.set(1)(I(3)) shouldEqual I(1)
    i.set(1)(S("")) shouldEqual S("")
  }

  test("setOption") {
    i.setOption(1)(I(3)) shouldEqual Some(I(1))
    i.setOption(1)(S("")) shouldEqual None
  }

  test("GenPrism nullary equality") {
    GenPrism[Arities, Nullary] composeIso GenIso.unit[Nullary] shouldEqual _nullary
  }

  test("GenPrism unary equality") {
    GenPrism[Arities, Unary] composeIso GenIso[Unary, Int] shouldEqual _unary
  }

  test("GenPrism binary equality") {
    GenPrism[Arities, Binary] composeIso GenIso.fields[Binary] shouldEqual _binary
  }

  test("GenPrism quintary equality") {
    GenPrism[Arities, Quintary] composeIso GenIso.fields[Quintary] shouldEqual _quintary
  }
}
