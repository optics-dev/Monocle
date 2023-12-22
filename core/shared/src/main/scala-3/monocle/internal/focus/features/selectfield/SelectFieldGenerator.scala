package monocle.internal.focus.features.selectfield

import monocle.internal.focus.FocusBase
import monocle.{Getter, Lens}
import scala.quoted.Quotes
import scala.util.control.NonFatal

private[focus] trait GetterGenerator {
  this: FocusBase =>

  import macroContext.reflect.*

  def generateGetter(fieldName: String, from: Term): Term =
    try
      from.tpe.typeSymbol.fieldMember(fieldName) match {
        case sym if sym.isNoSymbol => Select.unique(from, fieldName)
        case sym                   => Select(from, sym)
      }
    catch {
      case NonFatal(_) =>
        from.tpe.typeSymbol
          .methodMember(fieldName)
          .collect {
            case sym if sym.paramSymss == Nil => Select(from, sym)
          }
          .head
    }

}

private[focus] trait SelectFieldGenerator extends GetterGenerator {
  this: FocusBase =>

  import macroContext.reflect.*

  def generateSelectField(action: FocusAction.SelectField): Term = {
    import action.{fieldName, fromType, fromTypeArgs, toType}

    def generateSetter(from: Term, to: Term): Term =
      Select.overloaded(from, "copy", fromTypeArgs, NamedArg(fieldName, to) :: Nil) // o.copy(field = value)

    action.selectType match {
      case SelectType.CaseClassField =>
        (fromType.asType, toType.asType) match {
          case ('[f], '[t]) =>
            '{
              Lens.apply[f, t]((from: f) => ${ generateGetter(fieldName, '{ from }.asTerm).asExprOf[t] })((to: t) =>
                (from: f) => ${ generateSetter('{ from }.asTerm, '{ to }.asTerm).asExprOf[f] }
              )
            }.asTerm
        }
      case SelectType.PublicField =>
        (fromType.asType, toType.asType) match {
          case ('[f], '[t]) =>
            '{ Getter.apply[f, t]((from: f) => ${ generateGetter(fieldName, '{ from }.asTerm).asExprOf[t] }) }.asTerm
        }
      case SelectType.VirtualField =>
        (fromType.asType, toType.asType) match {
          case ('[f], '[t]) =>
            '{
              Getter.apply[f, t]((from: f) =>
                ${ generateGetter(fieldName, '{ from }.asTerm).appliedToArgs(List()).asExprOf[t] }
              )
            }.asTerm
        }

    }
  }
}
