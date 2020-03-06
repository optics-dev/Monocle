package monocle

import eu.timepit.refined._
import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.char.{LowerCase, UpperCase}
import eu.timepit.refined.string.{EndsWith, StartsWith}
import eu.timepit.refined.numeric.Interval

package object refined {
  type ZeroTo[T] = Int Refined Interval.Closed[W.`0`.T, T]

  type ByteBits = ZeroTo[W.`7`.T]
  type CharBits = ZeroTo[W.`15`.T]
  type IntBits  = ZeroTo[W.`31`.T]
  type LongBits = ZeroTo[W.`63`.T]

  type LowerCaseChar = Char Refined LowerCase
  type UpperCaseChar = Char Refined UpperCase

  type StartsWithString[T <: String] = String Refined StartsWith[T]
  type EndsWithString[T <: String]   = String Refined EndsWith[T]

  private[refined] def refinedPrism[T, P](implicit v: Validate[T, P]): Prism[T, T Refined P] =
    Prism.partial[T, T Refined P] {
      case t if v.isValid(t) => Refined.unsafeApply(t)
    } {
      _.value
    }
}
