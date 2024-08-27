package monocle.internal.focus.features.withdefault

import monocle.internal.focus.FocusBase
import monocle.std.option.withDefault

private[focus] trait WithDefaultGenerator {
  this: FocusBase =>

  import macroContext.reflect.*

  def generateWithDefault(action: FocusAction.KeywordWithDefault): Term = {
    import action.{toType, defaultValue}

    toType.asType match {
      case '[t] => '{ withDefault[t](${ defaultValue.asExprOf[t] }) }.asTerm
    }
  }
}
