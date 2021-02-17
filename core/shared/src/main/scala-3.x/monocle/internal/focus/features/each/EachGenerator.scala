package monocle.internal.focus.features.each

import monocle.function.Each
import monocle.internal.focus.FocusBase

private[focus] trait EachGenerator {
  this: FocusBase => 

  import macroContext.reflect._

  def generateEach(fromType: TypeRepr, toType: TypeRepr, eachInstance: Term): Term = 
    (fromType.asType, toType.asType) match {
      case ('[f], '[t]) => '{(${eachInstance.asExprOf[Each[f, t]]}.each)}.asTerm
    }
    
}