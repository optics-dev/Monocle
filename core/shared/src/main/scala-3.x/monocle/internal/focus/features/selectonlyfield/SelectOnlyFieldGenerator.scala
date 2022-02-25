package monocle.internal.focus.features.selectonlyfield

import monocle.internal.focus.FocusBase
import monocle.internal.focus.features.SelectGeneratorBase
import monocle.Iso

private[focus] trait SelectOnlyFieldGenerator {
  this: FocusBase with SelectGeneratorBase =>

  import macroContext.reflect._

  def generateSelectOnlyField(action: FocusAction.SelectOnlyField): Term = {
    import action.{fieldName, fromType, fromTypeArgs, fromCompanion, toType}

    def generateReverseGet(to: Term): Term =
      // Companion.apply(value)(implicits)*
      etaExpandIfNecessary(Select.overloaded(fromCompanion, "apply", fromTypeArgs, List(to)))

    (fromType.asType, toType.asType) match {
      case ('[f], '[t]) =>
        '{
          Iso.apply[f, t]((from: f) => ${ generateGetter('{ from }.asTerm, fieldName).asExprOf[t] })((to: t) =>
            ${ generateReverseGet('{ to }.asTerm).asExprOf[f] }
          )
        }.asTerm
    }
  }
}
