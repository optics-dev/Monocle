package monocle.internal.focus.features.selectonlyfield

import monocle.internal.focus.FocusBase
import monocle.internal.focus.features.SelectParserBase

private[focus] trait SelectOnlyFieldParser {
  this: FocusBase with SelectParserBase =>

  import this.macroContext.reflect._

  object SelectOnlyField extends FocusParser {

    def unapply(term: Term): Option[FocusResult[(RemainingCode, FocusAction)]] = term match {

      case Select(CaseClass(remainingCode, classSymbol), fieldName) if notACaseField(classSymbol, fieldName) =>
        Some(FocusError.NotACaseField(remainingCode.tpe.show, fieldName).asResult)

      case Select(CaseClass(remainingCode, classSymbol), fieldName) if hasOnlyOneField(classSymbol) =>
        val fromType = getType(remainingCode)
        val action = if (hasOnlyOneParameterList(classSymbol)) {
          getFieldAction(fromType, classSymbol, fieldName)
        } else {
          getFieldActionWithImplicits(fromType, classSymbol, fieldName)
        }
        val remainingCodeWithAction = action.map(a => (RemainingCode(remainingCode), a))
        Some(remainingCodeWithAction)

      case _ => None
    }
  }

  private def getFieldAction(
    fromType: TypeRepr,
    fromClassSymbol: Symbol,
    fieldName: String
  ): FocusResult[FocusAction] =
    for {
      toType <- getFieldType(fromType, fieldName)
      companion = getCompanionObject(fromClassSymbol)
      supplied  = getSuppliedTypeArgs(fromType)
    } yield FocusAction.SelectOnlyField(fieldName, fromType, supplied, companion, toType)

  private def getFieldActionWithImplicits(
    fromType: TypeRepr,
    fromClassSymbol: Symbol,
    fieldName: String
  ): FocusResult[FocusAction] =
    for {
      toType <- getFieldType(fromType, fieldName)
      companion = getCompanionObject(fromClassSymbol)
      supplied  = getSuppliedTypeArgs(fromType)
      reverseGet <- constructReverseGet(companion, fromType, toType, supplied)
    } yield FocusAction.SelectOnlyFieldWithImplicits(fieldName, fromType, toType, reverseGet)

  private def hasOnlyOneField(classSymbol: Symbol): Boolean =
    classSymbol.caseFields.length == 1

  private def notACaseField(classSymbol: Symbol, fieldName: String): Boolean =
    classSymbol.caseFields.forall(_.name != fieldName)

  private def hasOnlyOneParameterList(classSymbol: Symbol): Boolean =
    classSymbol.primaryConstructor.paramSymss match {
      case _ :: Nil                                    => true
      case (head :: _) :: _ :: Nil if head.isTypeParam => true
      case _                                           => false
    }

  private def getCompanionObject(classSymbol: Symbol): Term =
    Ref(classSymbol.companionModule)

  private case class LiftException(error: FocusError) extends Exception

  private def constructReverseGet(
    companion: Term,
    fromType: TypeRepr,
    toType: TypeRepr,
    fromTypeArgs: List[TypeRepr]
  ): FocusResult[Term] =
    // Companion.apply(value)(implicits)*
    (fromType.asType, toType.asType) match {
      case ('[f], '[t]) =>
        scala.util.Try('{ (to: t) =>
          ${
            etaExpandIfNecessary(
              Select.overloaded(companion, "apply", fromTypeArgs, List('{ to }.asTerm))
            ).fold(error => throw new LiftException(error), _.asExprOf[f])
          }
        }.asTerm) match {
          case scala.util.Success(term)                 => Right(term)
          case scala.util.Failure(LiftException(error)) => Left(error)
          case scala.util.Failure(other)                => Left(FocusError.ExpansionFailed(other.toString))
        }
    }
}
