package monocle.macros

import monocle.{Focus, Prism}
import monocle.syntax.all._

object GenPrism {
  inline def apply[Source, Target <: Source]: Prism[Source, Target] =
    Focus[Source]().as[Target]
}
