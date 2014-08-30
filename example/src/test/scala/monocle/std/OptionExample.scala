package monocle.std

import org.specs2.scalaz.Spec

import scalaz.Maybe


class OptionExample extends Spec {

  "some creates a Prism from an Option to its element" in {

    some.getMaybe(Some(1)) ==== Maybe.just(1)
    some.reverseGet(1)     ==== Some(1)

    // type can be changed with set and modify
    some.set('a')(Some(1))  ==== Some('a')
    some.set(2)(None)       ==== None

    some.modify((_: Int) + 2.0)(Some(1)) ==== Some(3.0)

  }

  "none creates a Prism from an Option to Unit" in {

    none.getMaybe(None)    ==== Maybe.just(())
    none.getMaybe(Some(2)) ==== Maybe.empty

    none.reverseGet(()) ==== None

    // none setter does nothing
    none.set(())(Some(1)) ==== Some(1)
    none.set(())(None   ) ==== None

  }

}
