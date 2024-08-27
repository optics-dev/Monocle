package monocle.internal.focus.features.selectfield

import monocle.internal.focus.FocusBase
import monocle.Lens
import scala.quoted.Quotes

private[focus] trait SelectFieldGenerator {
  this: FocusBase =>

  import macroContext.reflect.*

  def generateSelectField(action: FocusAction.SelectField): Term = {
    import action.{fieldName, fromType, fromTypeArgs, toType}

    def generateGetter(from: Term): Term =
      Select.unique(from, fieldName) // o.field

    def generateSetter(from: Term, to: Term): Term =
      Select.overloaded(from, "copy", fromTypeArgs, NamedArg(fieldName, to) :: Nil) // o.copy(field = value)

    (fromType.asType, toType.asType) match {
      case ('[f], '[t]) =>
        '{
          Lens.apply[f, t]((from: f) => ${ generateGetter('{ from }.asTerm).asExprOf[t] })((to: t) =>
            (from: f) => ${ generateSetter('{ from }.asTerm, '{ to }.asTerm).asExprOf[f] }
          )
        }.asTerm
    }
  }
}
