package monocle.internal.focus.features.withdefault

import monocle.internal.focus.FocusBase
import monocle.std.option.withDefault

private[focus] trait WithDefaultGenerator {
  this: FocusBase => 

  import macroContext.reflect._

  def generateWithDefault(toType: TypeRepr, defaultValue: Term): Term = {
    toType.asType match {
      case '[t] => '{ withDefault[t](${defaultValue.asExprOf[t]}) }.asTerm
    }
  }
}