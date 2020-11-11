package monocle

import monocle.law.discipline.{OptionalTests, PrismTests, SetterTests, TraversalTests}
import monocle.macros.{GenIso, GenPrism}
import cats.arrow.{Category, Compose}
import cats.syntax.either._

class PrismSpec extends MonocleSuite {
  def _right[E, A]: Prism[Either[E, A], A] =
    Prism[Either[E, A], A](_.toOption)(Either.right)
  def _pright[E, A]: Prism[Either[E, A], A] =
    Prism.partial[Either[E, A], A](Function.unlift(_.toOption))(Either.right)

  val _nullary: Prism[Arities, Unit] =
    Prism[Arities, Unit] {
      case Nullary() => Some(())
      case _         => None
    } { case () =>
      Nullary()
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
    assertEquals(
      Compose[Prism]
        .compose(_right[String, Int], _right[String, Either[String, Int]])
        .getOption(Right(Right(3))),
      Some(3)
    )
  }

  test("Prism has a Category instance") {
    assertEquals(Category[Prism].id[Int].getOption(3), Some(3))
  }

  test("only") {
    assertEquals(Prism.only(5).getOption(5), Some(()))
  }

  test("below") {
    val _5s = Prism.only(5).below[List]
    assertEquals(_5s.getOption(List(1, 2, 3, 4, 5)), None)
    assertEquals(_5s.getOption(List(5, 5, 5)), Some(List((), (), ())))
  }

  test("apply") {
    assertEquals(_nullary(), Nullary())
    assertEquals(_unary(3), Unary(3))
    assertEquals(_binary("foo", 7), Binary("foo", 7))
    assertEquals(_quintary('x', true, "bar", 13, 0.4), Quintary('x', true, "bar", 13, 0.4))
  }

  test("unapply") {
    // format: off
assertEquals(    ((Nullary(): Arities) match { case _nullary(unit) => unit }) ,  (()))
    // format: on
    assertEquals(((Unary(3): Arities) match { case _unary(value) => value * 2 }), 6)
    assertEquals(((Binary("foo", 7): Arities) match { case _binary(s, i) => s + i }), "foo7")
    assertEquals(
      ((Quintary('x', true, "bar", 13, 0.4): Arities) match {
        case _quintary(c, b, s, i, f) => "" + c + b + s + i + f
      }),
      "xtruebar130.4"
    )
  }

  sealed trait IntOrString
  case class I(i: Int)    extends IntOrString
  case class S(s: String) extends IntOrString

  val i = GenPrism[IntOrString, I] composeIso GenIso[I, Int]
  val s = Prism[IntOrString, String] { case S(s) => Some(s); case _ => None }(S.apply)

  test("getOption") {
    assertEquals(i.getOption(I(1)), Some(1))
    assertEquals(i.getOption(S("")), None)

    assertEquals(s.getOption(S("hello")), Some("hello"))
    assertEquals(s.getOption(I(10)), None)
  }

  test("reverseGet") {
    assertEquals(i.reverseGet(3), I(3))
    assertEquals(s.reverseGet("Yop"), S("Yop"))
  }

  test("isEmpty") {
    assertEquals(i.isEmpty(I(1)), false)
    assertEquals(i.isEmpty(S("")), true)
  }

  test("nonEmpty") {
    assertEquals(i.nonEmpty(I(1)), true)
    assertEquals(i.nonEmpty(S("")), false)
  }

  test("find") {
    assertEquals(i.find(_ > 5)(I(9)), Some(9))
    assertEquals(i.find(_ > 5)(I(2)), None)
  }

  test("exist") {
    assertEquals(i.exist(_ > 5)(I(9)), true)
    assertEquals(i.exist(_ > 5)(I(2)), false)
    assertEquals(i.exist(_ > 5)(S("")), false)
  }

  test("all") {
    assertEquals(i.all(_ > 5)(I(9)), true)
    assertEquals(i.all(_ > 5)(I(2)), false)
    assertEquals(i.all(_ > 5)(S("")), true)
  }

  test("modify") {
    assertEquals(i.modify(_ + 1)(I(3)), I(4))
    assertEquals(i.modify(_ + 1)(S("")), S(""))
  }

  test("modifyOption") {
    assertEquals(i.modifyOption(_ + 1)(I(3)), Some(I(4)))
    assertEquals(i.modifyOption(_ + 1)(S("")), None)
  }

  test("set") {
    assertEquals(i.set(1)(I(3)), I(1))
    assertEquals(i.set(1)(S("")), S(""))
  }

  test("setOption") {
    assertEquals(i.setOption(1)(I(3)), Some(I(1)))
    assertEquals(i.setOption(1)(S("")), None)
  }

  test("GenPrism nullary equality".ignore) {
    assertEquals(
      GenPrism[Arities, Nullary] composeIso GenIso
        .unit[Nullary],
      _nullary
    )
  }

  test("GenPrism unary equality".ignore) {
    assertEquals(GenPrism[Arities, Unary] composeIso GenIso[Unary, Int], _unary)
  }

  test("GenPrism binary equality".ignore) {
    assertEquals(
      GenPrism[Arities, Binary] composeIso GenIso
        .fields[Binary],
      _binary
    )
  }

  test("GenPrism quintary equality".ignore) {
    assertEquals(
      GenPrism[Arities, Quintary] composeIso GenIso
        .fields[Quintary],
      _quintary
    )
  }

  test("to") {
    assertEquals(i.to(_.toString()).getAll(I(1)), List("1"))
  }

  test("some") {
    case class SomeTest(y: Option[Int])
    val obj = SomeTest(Some(2))

    val prism = Iso[SomeTest, Option[Int]](_.y)(SomeTest).asPrism

    assertEquals(prism.some.getOption(obj), Some(2))
    assertEquals(obj.applyPrism(prism).some.getOption, Some(2))
  }

  test("withDefault") {
    case class SomeTest(y: Option[Int])
    val objSome = SomeTest(Some(2))
    val objNone = SomeTest(None)

    val prism = Iso[SomeTest, Option[Int]](_.y)(SomeTest).asPrism

    assertEquals(prism.withDefault(0).getOption(objSome), Some(2))
    assertEquals(prism.withDefault(0).getOption(objNone), Some(0))

    assertEquals(objNone.applyPrism(prism).withDefault(0).getOption, Some(0))
  }

  test("each") {
    case class SomeTest(y: List[Int])
    val obj = SomeTest(List(1, 2, 3))

    val prism = Iso[SomeTest, List[Int]](_.y)(SomeTest).asPrism

    assertEquals(prism.each.getAll(obj), List(1, 2, 3))
    assertEquals(obj.applyPrism(prism).each.getAll, List(1, 2, 3))
  }
}
