package monocle.internal.focus.features.selectonlyfield

import monocle.internal.focus.FocusBase
import monocle.internal.focus.features.SelectParserBase

private[focus] trait SelectOnlyFieldParser {
  this: FocusBase with SelectParserBase => 

  import this.macroContext.reflect._
  
  object SelectOnlyField extends FocusParser {

    def unapply(term: Term): Option[FocusResult[(RemainingCode, FocusAction)]] = term match {
      
      case Select(CaseClass(remainingCode), fieldName) if hasOnlyOneField(remainingCode) => 
        val fromType = getType(remainingCode)
        val action = getFieldAction(fromType, fieldName)
        val remainingCodeWithAction = action.map(a => (RemainingCode(remainingCode), a))
        Some(remainingCodeWithAction)

      case _ => None
    }
  }

  private def getFieldAction(fromType: TypeRepr, fieldName: String): FocusResult[FocusAction] = 
    for {
      toType <- getFieldType(fromType, fieldName)
      companion <- getCompanionObject(fromType)
      supplied = getSuppliedTypeArgs(fromType)
    } yield FocusAction.SelectOnlyField(fieldName, fromType, supplied, companion, toType)

  private def hasOnlyOneField(fromCode: Term): Boolean = 
    getType(fromCode).classSymbol.exists(_.caseFields.length == 1)

  private def getCompanionObject(fromType: TypeRepr): FocusResult[Term] =
    getClassSymbol(fromType).map(sym => Ref(sym.companionModule))
}