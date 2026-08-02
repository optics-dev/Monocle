package monocle.internal.focus.features.selectfield

import monocle.internal.focus.FocusBase
import monocle.Lens
import monocle.Iso
import scala.quoted.Quotes
import scala.quoted.Expr

private[focus] trait SelectNamedTupleFieldGenerator {
  this: FocusBase =>

  import macroContext.reflect.*

  def generateSelectNamedTupleField(action: FocusAction.SelectNamedTupleField): Term = {
    def generateGetter(from: Term): Term = action.namedTuples.accessFieldByName(from, action)

    def generateSetter(from: Term, to: Term): Term =
      action.namedTuples.reconstructWithUpdatedField(from, action, to)

    def generateReverseGet(to: Expr[Any]): Term =
      action.namedTuples.construct(action.fromDescription, Vector(to))

    (action.fromDescription.sourceType.asType, action.toType.asType) match {
      case ('[f], '[t]) =>
        if (action.fromDescription.values.size == 1) {
          '{
            Iso.apply[f, t]((from: f) => ${ generateGetter('from.asTerm).asExprOf[t] })((to: t) =>
              ${ generateReverseGet('to).asExprOf[f] }
            )
          }.asTerm
        } else {
          '{
            Lens.apply[f, t]((from: f) => ${ generateGetter('from.asTerm).asExprOf[t] })((to: t) =>
              (from: f) => ${ generateSetter('from.asTerm, 'to.asTerm).asExprOf[f] }
            )
          }.asTerm
        }
    }

  }
}
