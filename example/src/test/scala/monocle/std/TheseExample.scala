package monocle.std

import org.specs2.scalaz.Spec
import scalaz._
import scalaz.\&/._
import scalaz.syntax.either._
import monocle.std.these._

class TheseExample extends Spec {
  "theseDisjunction is a prism between These and a Disjunction" in {
    theseDisjunction.getMaybe(This(5)        : Int \&/ String) ==== Maybe.just(5.left[String])
    theseDisjunction.getMaybe(That("Hello")  : Int \&/ String) ==== Maybe.just("Hello".right[Int])
    theseDisjunction.getMaybe(Both(5,"Hello"): Int \&/ String) ==== Maybe.empty[Int \/ String]

    theseDisjunction.reverseGet(-\/(5)      : Int \/ String) ==== (This(5)      : Int \&/ String)
    theseDisjunction.reverseGet(\/-("Hello"): Int \/ String) ==== (That("Hello"): Int \&/ String)
  }
}
