package monocle.internal.focus.features.optionsome

import monocle.internal.focus.FocusBase
import monocle.std.option.some

private[focus] trait OptionSomeGenerator {
  this: FocusBase => 

  import macroContext.reflect._

  def generateOptionSome(toType: TypeRepr): Term = {
    toType.asType match {
      case '[t] => '{ some[t] }.asTerm
    }
  }
}