package monocle.internal.focus.features.at

import monocle.function.At
import monocle.internal.focus.FocusBase

private[focus] trait AtGenerator {
  this: FocusBase => 

  import macroContext.reflect._

  def generateAt(action: FocusAction.KeywordAt): Term = {
    import action.{fromType, toType, index, atInstance}

    (fromType.asType, index.tpe.asType, toType.asType) match {
      case ('[f], '[i], '[t]) => '{(${atInstance.asExprOf[At[f, i, t]]}.at(${index.asExprOf[i]}))}.asTerm
    }
  }
    
}