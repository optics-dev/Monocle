package monocle.internal.focus.features.selectonlyfield

import monocle.internal.focus.FocusBase
import monocle.Iso
import scala.quoted.Quotes
import monocle.internal.focus.features.selectfield.GetterGenerator

import scala.util.control.NonFatal

private[focus] trait SelectOnlyFieldGenerator extends GetterGenerator {
  this: FocusBase =>

  import macroContext.reflect._

  def generateSelectOnlyField(action: FocusAction.SelectOnlyField): Term = {
    import action.{fieldName, fromType, fromTypeArgs, fromCompanion, toType}

    def generateReverseGet(to: Term): Term =
      Select.overloaded(fromCompanion, "apply", fromTypeArgs, List(to)) // Companion.apply(value)

    (fromType.asType, toType.asType) match {
      case ('[f], '[t]) =>
        '{
          Iso.apply[f, t]((from: f) => ${ generateGetter(fieldName, '{ from }.asTerm).asExprOf[t] })((to: t) =>
            ${ generateReverseGet('{ to }.asTerm).asExprOf[f] }
          )
        }.asTerm
    }
  }
}
