package monocle.internal.focus.features.selectfield

import monocle.internal.focus.FocusBase
import monocle.Lens

private[focus] trait SelectFieldGenerator {
  this: FocusBase =>

  import macroContext.reflect._

  def generateSelectField(action: FocusAction.SelectField): Term = {
    import action.{fieldName, fromType, toType, setter}

    (fromType.asType, toType.asType) match {
      case ('[f], '[t]) =>
        '{
          Lens.apply[f, t]((from: f) => ${ Select.unique('{ from }.asTerm, fieldName).asExprOf[t] })(
            ${ setter.asExprOf[t => f => f] }
          )
        }.asTerm
    }
  }
}
