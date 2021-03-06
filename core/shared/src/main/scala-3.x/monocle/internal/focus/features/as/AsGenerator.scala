package monocle.internal.focus.features.as

import monocle.Prism
import monocle.internal.focus.FocusBase

private[focus] trait AsGenerator {
  this: FocusBase => 

  import macroContext.reflect._

  def generateAs(action: FocusAction.KeywordAs): Term = {
    import action.{fromType, toType}

    (fromType.asType, toType.asType) match {
      case ('[f], '[t]) => '{ 
        Prism[f, t]((from: f) => if (from.isInstanceOf[t]) Some(from.asInstanceOf[t]) else None)
                   ((to: t) => to.asInstanceOf[f]) }.asTerm
    }
  }
}