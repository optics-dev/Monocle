package monocle.internal

import monocle.Iso
import monocle.syntax.all._

final class IsoFieldsTest extends munit.FunSuite {

  test("Iso.fields works with empty tuple") {
    case object Foo
    val iso: Iso[Foo.type, EmptyTuple] = Iso.fields[Foo.type]

    assertEquals(iso.get(Foo), EmptyTuple)
    assertEquals(iso.reverseGet(EmptyTuple), Foo)
    assertEquals(iso.reverseGet(iso.get(Foo)), Foo)
  }

  test("Iso.fields works with length-one tuple") {
    case class Foo(s: String)
    val iso: Iso[Foo, String *: EmptyTuple] = Iso.fields[Foo]

    assertEquals(iso.get(Foo("abc")), "abc" *: EmptyTuple)
    assertEquals(iso.reverseGet("abc" *: EmptyTuple), Foo("abc"))
    assertEquals(iso.reverseGet(iso.get(Foo("abc"))), Foo("abc"))
  }

  test("Iso.fields works with length-two tuple") {
    case class Foo(s: String, i: Int)
    val iso: Iso[Foo, (String, Int)] = Iso.fields[Foo]

    assertEquals(iso.get(Foo("hi", 5)), ("hi", 5))
    assertEquals(iso.reverseGet(("hi", 5)), Foo("hi", 5))
    assertEquals(iso.reverseGet(iso.get(Foo("hi", 5))), Foo("hi", 5))
  }
}
