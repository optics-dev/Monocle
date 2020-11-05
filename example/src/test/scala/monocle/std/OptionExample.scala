package monocle.std

import monocle.MonocleSuite

class OptionExample extends MonocleSuite {
  import monocle.std.option.{none => mNone, some => mSome}

  test("some creates a Prism from an Option to its element") {
    assertEquals(mSome.getOption(Some(1)), Some(1))
    assertEquals(mSome.reverseGet(1), Some(1))

    // type can be changed with set and modify
    assertEquals(mSome.set('a')(Some(1)), Some('a'))
    assertEquals(mSome.set(2)(None), None)

    assertEquals(pSome.modify((_: Int) + 2.0)(Some(1)), Some(3.0))
  }

  test("none creates a Prism from an Option to Unit") {
    assertEquals(mNone.getOption(None), Some(()))
    assertEquals(mNone.getOption(Some(2)), None)

    assertEquals(mNone.reverseGet(()), None)

    // none setter does nothing
    assertEquals(mNone.set(())(Some(1)), Some(1))
    assertEquals(mNone.set(())(None), None)
  }
}
