package monocle.refined

import monocle._

object chars extends CharsInstances

trait CharsInstances {
  val lowerCase: Prism[Char, LowerCaseChar] = refinedPrism
  val upperCase: Prism[Char, UpperCaseChar] = refinedPrism
}
