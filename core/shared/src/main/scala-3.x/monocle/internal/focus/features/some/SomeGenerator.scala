package monocle.internal.focus.features.some

import monocle.internal.focus.FocusBase
import monocle.std.option.some

private[focus] trait SomeGenerator {
  this: FocusBase => 

  import macroContext.reflect._

  def generateSome(toType: TypeRepr): Term = {
    toType.asType match {
      case '[t] => '{ some[t] }.asTerm
    }
  }
}