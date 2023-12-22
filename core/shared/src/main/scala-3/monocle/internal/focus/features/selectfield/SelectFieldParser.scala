package monocle.internal.focus.features.selectfield

import monocle.internal.focus.FocusBase
import monocle.internal.focus.features.SelectParserBase

private[focus] trait SelectFieldParser {
  this: FocusBase with SelectParserBase =>

  import this.macroContext.reflect._

  object SelectField extends FocusParser {

    def unapply(term: Term): Option[FocusResult[(RemainingCode, FocusAction)]] = term match {
      case Select(CaseClass(remainingCode), fieldName) =>
        val fromType                = getType(remainingCode)
        val action                  = getFieldAction(fromType, fieldName, SelectType.CaseClassField, term.pos)
        val remainingCodeWithAction = action.map(a => (RemainingCode(remainingCode), a))
        Some(remainingCodeWithAction)

      case Select(remainingCode, fieldName) =>
        val fromType                = getType(remainingCode)
        val action                  = getFieldAction(fromType, fieldName, SelectType.PublicField, term.pos)
        val remainingCodeWithAction = action.map(a => (RemainingCode(remainingCode), a))
        Some(remainingCodeWithAction)
      case Apply(Select(remainingCode, fieldName), List()) =>
        val fromType = getType(remainingCode)
        val action = getVirtualFieldType(fromType, fieldName, term.pos).flatMap { toType =>
          Right(
            FocusAction.SelectField(fieldName, fromType, getSuppliedTypeArgs(fromType), toType, SelectType.VirtualField)
          )
        }
        val remainingCodeWithAction = action.map(a => (RemainingCode(remainingCode), a))
        Some(remainingCodeWithAction)
      case _ => None
    }
  }

  private def getFieldAction(
    fromType: TypeRepr,
    fieldName: String,
    selectType: SelectType,
    pos: Position
  ): FocusResult[FocusAction] =
    getFieldType(fromType, fieldName, pos).flatMap { toType =>
      Right(FocusAction.SelectField(fieldName, fromType, getSuppliedTypeArgs(fromType), toType, selectType))
    }
}
