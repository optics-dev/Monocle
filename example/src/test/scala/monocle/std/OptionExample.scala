package monocle.std

import monocle.MonocleSuite

class OptionExample extends MonocleSuite {

  import monocle.std.{none => mNone, some => mSome}

  test("some creates a Prism from an Option to its element") {
    mSome.getOption(Some(1)) shouldEqual Some(1)
    mSome.reverseGet(1)      shouldEqual Some(1)

    // type can be changed with set and modify
    mSome.set('a')(Some(1))  shouldEqual Some('a')
    mSome.set(2)(None)       shouldEqual None

    pSome.modify((_: Int) + 2.0)(Some(1)) shouldEqual Some(3.0)
  }

  test("none creates a Prism from an Option to Unit") {
    mNone.getOption(None)    shouldEqual Some(())
    mNone.getOption(Some(2)) shouldEqual None

    mNone.reverseGet(()) shouldEqual None

    // none setter does nothing
    mNone.set(())(Some(1)) shouldEqual Some(1)
    mNone.set(())(None   ) shouldEqual None
  }

}
