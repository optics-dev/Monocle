package monocle.internal.focus.features.selectonlyfield

import monocle.internal.focus.FocusBase
import monocle.Iso

private[focus] trait SelectOnlyFieldGenerator {
  this: FocusBase =>

  import macroContext.reflect._

  private def generateGetter(from: Term, fieldName: String): Term =
    Select.unique(from, fieldName) // o.field

  def generateSelectOnlyField(action: FocusAction.SelectOnlyField): Term = {
    import action.{fieldName, fromType, fromTypeArgs, fromCompanion, toType}

    def generateReverseGet(to: Term): Term =
      Select.overloaded(fromCompanion, "apply", fromTypeArgs, List(to)) // Companion.apply(value)

    (fromType.asType, toType.asType) match {
      case ('[f], '[t]) =>
        '{
          Iso.apply[f, t]((from: f) => ${ generateGetter('{ from }.asTerm, fieldName).asExprOf[t] })((to: t) =>
            ${ generateReverseGet('{ to }.asTerm).asExprOf[f] }
          )
        }.asTerm
    }
  }

  def generateSelectOnlyFieldWithImplicits(action: FocusAction.SelectOnlyFieldWithImplicits): Term = {
    import action.{fieldName, fromType, toType, reverseGet}

    (fromType.asType, toType.asType) match {
      case ('[f], '[t]) =>
        '{
          Iso.apply[f, t]((from: f) => ${ generateGetter('{ from }.asTerm, fieldName).asExprOf[t] })(
            ${ reverseGet.asExprOf[t => f] }
          )
        }.asTerm
    }
  }
}
