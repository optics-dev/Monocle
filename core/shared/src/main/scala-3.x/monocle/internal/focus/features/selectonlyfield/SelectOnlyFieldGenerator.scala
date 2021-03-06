package monocle.internal.focus.features.selectonlyfield

import monocle.internal.focus.FocusBase
import monocle.Iso
import scala.quoted.Quotes

private[focus] trait SelectOnlyFieldGenerator {
  this: FocusBase => 

  import macroContext.reflect._

  def generateSelectOnlyField(action: FocusAction.SelectOnlyField): Term = {
    import action.{fieldName, fromType, fromTypeArgs, fromCompanion, toType}

    def generateGetter(from: Term): Term = 
      Select.unique(from, fieldName) // o.field

    def generateReverseGet(to: Term): Term = 
      Select.overloaded(fromCompanion, "apply", fromTypeArgs, List(to)) // Companion.apply(value)

    (fromType.asType, toType.asType) match {
      case ('[f], '[t]) => 
        '{
          Iso.apply[f, t]((from: f) => ${ generateGetter('{from}.asTerm).asExprOf[t] })
                         ((to: t) => ${ generateReverseGet('{to}.asTerm).asExprOf[f] })
        }.asTerm
    }
  }
}