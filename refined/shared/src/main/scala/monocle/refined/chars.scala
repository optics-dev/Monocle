package monocle.refined

import eu.timepit.refined.api.Refined
import eu.timepit.refined.char.{LowerCase, UpperCase}
import monocle._


object chars extends CharsInstances

trait CharsInstances {
  val lowerCase: Prism[Char, LowerCaseChar] = toCase[LowerCase](c => c.isLower)
  val upperCase: Prism[Char, UpperCaseChar] = toCase[UpperCase](c => c.isUpper)

  private def toCase[P](p: Char => Boolean): Prism[Char, Refined[Char, P]] = {
    Prism[Char, Refined[Char, P]] {
      case char if p(char) => Some(Refined.unsafeApply(char))
      case _ => None
    } {_.get}
  }
}

