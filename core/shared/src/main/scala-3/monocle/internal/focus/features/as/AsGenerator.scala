package monocle.internal.focus.features.as

import monocle.internal.focus.FocusBase
import monocle.internal.AsPrismImpl

private[focus] trait AsGenerator {
  this: FocusBase =>

  import macroContext.reflect._

  def generateAs(action: FocusAction.KeywordAs): Term = {
    import action.{fromType, toType}

    (fromType.asType, toType.asType) match {
      case ('[f], '[t]) => AsPrismImpl[f, t].asTerm
    }
  }
}
