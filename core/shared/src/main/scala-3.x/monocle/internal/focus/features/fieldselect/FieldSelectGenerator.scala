package monocle.internal.focus.features.fieldselect

import monocle.internal.focus.FocusBase
import monocle.Lens
import scala.quoted.Quotes

private[focus] trait FieldSelectGenerator {
  this: FocusBase => 

  import macroContext.reflect._

  def generateFieldSelect(field: String, fromType: TypeRepr, fromTypeArgs: List[TypeRepr], toType: TypeRepr): Term = {
    (fromType.asType, toType.asType) match {
      case ('[f], '[t]) => 
        '{
          val setter: t => f => f = (to: t) => (from: f) => 
            ${ generateSetter(field, '{from}.asTerm, '{to}.asTerm, fromTypeArgs).asExprOf[f] }

          val getter: f => t = (from: f) => 
            ${ generateGetter(field, '{from}.asTerm).asExprOf[t] }

          _root_.monocle.Lens.apply[f, t](getter)(setter)
        }.asTerm
    }
  }

  private def generateGetter(field: String, from: Term): Term = 
    Select.unique(from, field) // o.field

  private def generateSetter(field: String, from: Term, to: Term, fromTypeArgs: List[TypeRepr]): Term = {
    Select.overloaded(from, "copy", fromTypeArgs, NamedArg(field, to) :: Nil) // o.copy(field = value)
  }
}