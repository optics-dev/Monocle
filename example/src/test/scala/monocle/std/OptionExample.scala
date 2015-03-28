package monocle.std

import org.specs2.scalaz.Spec

class OptionExample extends Spec {

  import monocle.std.{some => mSome, none => mNone}

  "some creates a Prism from an Option to its element" in {
    mSome.getOption(Some(1)) ==== Some(1)
    mSome.reverseGet(1)      ==== Some(1)

    // type can be changed with set and modify
    mSome.set('a')(Some(1))  ==== Some('a')
    mSome.set(2)(None)       ==== None

    mSome.modify((_: Int) + 2.0)(Some(1)) ==== Some(3.0)
  }

  "none creates a Prism from an Option to Unit" in {
    mNone.getOption(None)    ==== Some(())
    mNone.getOption(Some(2)) ==== None

    mNone.reverseGet(()) ==== None

    // none setter does nothing
    mNone.set(())(Some(1)) ==== Some(1)
    mNone.set(())(None   ) ==== None
  }

}
