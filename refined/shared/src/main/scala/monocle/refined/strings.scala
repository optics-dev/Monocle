package monocle.refined

import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.string.{EndsWith, StartsWith}
import monocle._

object strings extends StringsInstances

trait StringsInstances {

  def startsWith(prefix: String)(implicit v: Validate[String, StartsWith[prefix.type]]): Prism[String, StartsWithString[prefix.type]] =
    refinedPrism[String, StartsWith[prefix.type]](prefix)

  def endsWith(suffix: String)(implicit v: Validate[String, EndsWith[suffix.type]]): Prism[String, EndsWithString[suffix.type]] =
    refinedPrism[String, EndsWith[suffix.type]](suffix)

  private def refinedPrism[T, P](t: T)(implicit v: Validate[T, P]): Prism[T, T Refined P] = {
    Prism.partial[T, Refined[T, P]] {
      case tt if v.isValid(t) => Refined.unsafeApply[T, P](tt)
    } {
      _.value
    }
  }
}
