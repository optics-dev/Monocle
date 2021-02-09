package monocle.internal.focus.features.optionsome

import monocle.internal.focus.FocusBase

private[focus] trait OptionSomeGenerator {
  this: FocusBase => 

  import macroContext.reflect._

  def generateOptionSome(toType: TypeRepr): Term = {
    toType.asType match {
      case '[t] => '{ _root_.monocle.std.option.some[t] }.asTerm
    }
  }
}