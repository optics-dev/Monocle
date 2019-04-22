package monocle.refined

import eu.timepit.refined.api.Validate
import eu.timepit.refined.string.{EndsWith, StartsWith}
import monocle._

object strings extends StringsInstances

trait StringsInstances {

  def startsWith(prefix: String)(implicit v: Validate[String, StartsWith[prefix.type]]): Prism[String, StartsWithString[prefix.type]] =
    refinedPrism

  def endsWith(suffix: String)(implicit v: Validate[String, EndsWith[suffix.type]]): Prism[String, EndsWithString[suffix.type]] =
    refinedPrism
}
