package monocle.syntax

import monocle.{Focus, MonocleSuite}

class FocusSyntax extends MonocleSuite {

  test("Focus[XXX] creates an Iso") {
    assertEquals(
      Focus[String]().get("hello"),
      "hello"
    )
  }

  test("foo.focus() creates an ApplyIso") {
    assertEquals(
      "hello".focus().get,
      "hello"
    )
  }

}
