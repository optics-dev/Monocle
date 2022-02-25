package monocle.internal.focus.features.selectfield

import monocle.internal.focus.FocusBase
import monocle.internal.focus.features.SelectGeneratorBase
import monocle.Lens

private[focus] trait SelectFieldGenerator {
  this: FocusBase with SelectGeneratorBase =>

  import macroContext.reflect._

  def generateSelectField(action: FocusAction.SelectField): Term = {
    import action.{fieldName, fromType, fromTypeArgs, toType}

    def generateSetter(from: Term, to: Term): Term =
      // o.copy(field = value)(implicits)*
      etaExpandIfNecessary(Select.overloaded(from, "copy", fromTypeArgs, NamedArg(fieldName, to) :: Nil))

    (fromType.asType, toType.asType) match {
      case ('[f], '[t]) =>
        '{
          Lens.apply[f, t]((from: f) => ${ generateGetter('{ from }.asTerm, fieldName).asExprOf[t] })((to: t) =>
            (from: f) => ${ generateSetter('{ from }.asTerm, '{ to }.asTerm).asExprOf[f] }
          )
        }.asTerm
    }
  }
}
