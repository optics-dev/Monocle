package monocle.internal.focus.features.selectfield

import monocle.internal.focus.FocusBase
import monocle.internal.focus.features.SelectParserBase

private[focus] trait SelectFieldParser {
  this: FocusBase with SelectParserBase => 

  import this.macroContext.reflect._
  
  object SelectField extends FocusParser {

    def unapply(term: Term): Option[FocusResult[(RemainingCode, FocusAction)]] = term match {
      
      case Select(CaseClass(remainingCode), fieldName) => 
        val fromType = getType(remainingCode)
        val action = getFieldAction(fromType, fieldName)
        val remainingCodeWithAction = action.map(a => (RemainingCode(remainingCode), a))
        Some(remainingCodeWithAction)

      case Select(remainingCode, fieldName) => 
        Some(FocusError.NotACaseClass(remainingCode.tpe.show, fieldName).asResult)
        
      case _ => None
    }
  }

  private def getFieldAction(fromType: TypeRepr, fieldName: String): FocusResult[FocusAction] = {
    getFieldType(fromType, fieldName).flatMap { toType => 
      Right(FocusAction.SelectField(fieldName, fromType, getSuppliedTypeArgs(fromType), toType))
    }
  }
}