package monocle.internal.focus.features.selectonlyfield

import monocle.internal.focus.FocusBase
import monocle.Iso

private[focus] trait SelectOnlyFieldGenerator {
  this: FocusBase =>

  import macroContext.reflect._

  def generateSelectOnlyField(action: FocusAction.SelectOnlyField): Term = {
    import action.{fieldName, fromType, toType, reverseGet}

    (fromType.asType, toType.asType) match {
      case ('[f], '[t]) =>
        '{
          Iso.apply[f, t]((from: f) => ${ Select.unique('{ from }.asTerm, fieldName).asExprOf[t] })(
            ${ reverseGet.asExprOf[t => f] }
          )
        }.asTerm
    }
  }
}
