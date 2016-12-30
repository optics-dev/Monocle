package monocle.refined

import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.StartsWith
import monocle._

object startswith extends StartsWithInstances

trait StartsWithInstances {

  def startsWith(prefix: String): Prism[String, StartsWithString[prefix.type]] = {
    Prism.partial[String, Refined[String, StartsWith[prefix.type]]] {
      case string if string.startsWith(prefix) => Refined.unsafeApply(string)
    }{_.value}
  }

}
