package monocle.refined

import eu.timepit.refined.api.{Validate, Refined}
import eu.timepit.refined.string.StartsWith
import monocle._

object strings extends StringsInstances

trait StringsInstances {

  def startsWith(prefix: String)(implicit v: Validate[String, StartsWith[prefix.type]]): Prism[String, StartsWithString[prefix.type]] = {
    Prism.partial[String, Refined[String, StartsWith[prefix.type]]] {
      case string if v.isValid(prefix) => Refined.unsafeApply(string)
    }{_.value}
  }

}
