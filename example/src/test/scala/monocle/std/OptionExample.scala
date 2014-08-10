package monocle.std

import org.specs2.scalaz.Spec


class OptionExample extends Spec {

  "some creates a Prism from an Option to its element" in {

    some.getOption(Some(1)) shouldEqual Some(1)
    some.reverseGet(1)      shouldEqual Some(1)

    // type can be changed with set and modify
    some.set(Some(1), 'a')  shouldEqual Some('a')
    some.set(None, 2)       shouldEqual None

    some.modify(Some(1), { (_: Int) + 2.0 }) shouldEqual Some(3.0)

  }

  "none creates a Prism from an Option to Unit" in {

    none.getOption(None)    shouldEqual Some(())
    none.getOption(Some(2)) shouldEqual None

    none.reverseGet(()) shouldEqual None

    // none setter doesn't do anything
    none.set(Some(1), ()) shouldEqual Some(1)
    none.set(None   , ()) shouldEqual None

  }

}
