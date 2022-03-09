package monocle.internal.focus.features.selectfield

import monocle.internal.focus.FocusBase
import monocle.Iso
import monocle.Lens

private[focus] trait SelectFieldGenerator {
  this: FocusBase =>

  import macroContext.reflect._

  private def generateGetter(from: Term, fieldName: String): Term =
    Select.unique(from, fieldName) // o.field

  def generateSelectField(action: FocusAction.SelectField): Term = {
    import action.{fieldName, fromType, fromTypeArgs, toType}

    def generateSetter(from: Term, to: Term): Term =
      Select.overloaded(from, "copy", fromTypeArgs, NamedArg(fieldName, to) :: Nil) // o.copy(field = value)

    (fromType.asType, toType.asType) match {
      case ('[f], '[t]) =>
        '{
          Lens.apply[f, t]((from: f) => ${ generateGetter('{ from }.asTerm, fieldName).asExprOf[t] })((to: t) =>
            (from: f) => ${ generateSetter('{ from }.asTerm, '{ to }.asTerm).asExprOf[f] }
          )
        }.asTerm
    }
  }

  def generateSelectFieldWithImplicits(action: FocusAction.SelectFieldWithImplicits): Term = {
    import action.{fieldName, fromType, toType, setter}

    (fromType.asType, toType.asType) match {
      case ('[f], '[t]) =>
        '{
          Lens.apply[f, t]((from: f) => ${ generateGetter('{ from }.asTerm, fieldName).asExprOf[t] })(
            ${ setter.asExprOf[t => f => f] }
          )
        }.asTerm
    }
  }

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
