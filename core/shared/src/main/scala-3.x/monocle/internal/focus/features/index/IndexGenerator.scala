package monocle.internal.focus.features.index

import monocle.function.Index
import monocle.internal.focus.FocusBase

private[focus] trait IndexGenerator {
  this: FocusBase => 

  import macroContext.reflect._

  def generateIndex(action: FocusAction.KeywordIndex): Term = {
    import action.{fromType, toType, index, indexInstance}

    (fromType.asType, index.tpe.widen.asType, toType.asType) match {
      case ('[f], '[i], '[t]) => '{(${indexInstance.asExprOf[Index[f, i, t]]}.index(${index.asExprOf[i]}))}.asTerm
    }
  }
    
}