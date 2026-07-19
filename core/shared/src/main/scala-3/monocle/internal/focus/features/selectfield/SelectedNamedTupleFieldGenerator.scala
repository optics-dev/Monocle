package monocle.internal.focus.features.selectfield

import monocle.internal.focus.FocusBase
import monocle.Lens
import scala.quoted.Quotes

private[focus] trait SelectNamedTupleFieldGenerator {
  this: FocusBase =>

  import macroContext.reflect.*

  def generateSelectNamedTupleField(action: FocusAction.SelectNamedTupleField): Term = {

    def generateGetter(from: Term): Term = ???

    def generateSetter(from: Term, to: Term): Term = ???

    // (fromType.asType, toType.asType) match {
    //   case ('[f], '[t]) =>
    //     '{
    //       Lens.apply[f, t]((from: f) => ${ generateGetter('from.asTerm).asExprOf[t] })((to: t) =>
    //         (from: f) => ${ generateSetter('from.asTerm, 'to.asTerm).asExprOf[f] }
    //       )
    //     }.asTerm
    // }

    ???
  }
}
