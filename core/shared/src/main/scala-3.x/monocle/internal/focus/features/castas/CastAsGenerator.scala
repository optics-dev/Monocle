package monocle.internal.focus.features.castas

import monocle.Prism
import monocle.internal.focus.FocusBase

private[focus] trait CastAsGenerator {
  this: FocusBase => 

  import macroContext.reflect._

  def generateCastAs(fromType: TypeRepr, toType: TypeRepr): Term = {
    (fromType.asType, toType.asType) match {
      case ('[f], '[t]) => '{ 
        Prism[f, t]((from: f) => if (from.isInstanceOf[t]) Some(from.asInstanceOf[t]) else None)
                   ((to: t) => to.asInstanceOf[f]) }.asTerm
    }
  }
}