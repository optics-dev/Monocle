package monocle.std

import org.specs2.scalaz.Spec
import scalaz._
import scalaz.\&/._
import scalaz.syntax.either._
import monocle.std.these._

class TheseExample extends Spec {
  "theseDisjunction is a prism between These and a Disjunction" in {
    theseToDisjunction.getMaybe(This(5)        : Int \&/ String) ==== Maybe.just(5.left[String])
    theseToDisjunction.getMaybe(That("Hello")  : Int \&/ String) ==== Maybe.just("Hello".right[Int])
    theseToDisjunction.getMaybe(Both(5,"Hello"): Int \&/ String) ==== Maybe.empty[Int \/ String]

    theseToDisjunction.reverseGet(-\/(5)      : Int \/ String) ==== (This(5)      : Int \&/ String)
    theseToDisjunction.reverseGet(\/-("Hello"): Int \/ String) ==== (That("Hello"): Int \&/ String)
  }
}
