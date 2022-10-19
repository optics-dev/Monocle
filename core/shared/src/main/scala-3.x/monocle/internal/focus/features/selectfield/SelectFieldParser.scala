package monocle.internal.focus.features.selectfield

import monocle.internal.focus.FocusBase
import monocle.internal.focus.features.SelectParserBase

private[focus] trait SelectFieldParser {
  this: FocusBase with SelectParserBase =>

  import this.macroContext.reflect._

  object SelectField extends FocusParser {

    def unapply(term: Term): Option[FocusResult[(RemainingCode, FocusAction)]] = term match {

      case Select(remainingCode @ CaseClassExtractor(caseClass: CaseClass), fieldName) =>
        Some(
          for {
            _               <- caseClass.allOtherParametersAreImplicitResult
            caseFieldSymbol <- caseClass.getCaseFieldSymbol(fieldName)
            action <- (caseClass.hasOnlyOneParameterList, caseClass.hasOnlyOneCaseField) match {
              case (true, false)  => getSelectFieldAction(caseClass, caseFieldSymbol)
              case (false, false) => getSelectFieldActionWithImplicits(caseClass, caseFieldSymbol)
              case (true, true)   => getSelectOnlyFieldAction(caseClass, caseFieldSymbol)
              case (false, true)  => getSelectOnlyFieldActionWithImplicits(caseClass, caseFieldSymbol)
            }
          } yield (RemainingCode(remainingCode), action)
        )

      case Select(remainingCode, fieldName) =>
        Some(FocusError.NotACaseClass(remainingCode.tpe.show, fieldName).asResult)

      case _ => None
    }
  }

  private def getSelectFieldAction(
    caseClass: CaseClass,
    caseFieldSymbol: Symbol
  ): FocusResult[FocusAction] =
    for {
      toType <- caseClass.getCaseFieldType(caseFieldSymbol)
    } yield FocusAction.SelectField(
      caseFieldSymbol,
      caseClass.typeRepr,
      caseClass.typeArgs,
      toType
    )

  private def getSelectFieldActionWithImplicits(
    caseClass: CaseClass,
    caseFieldSymbol: Symbol
  ): FocusResult[FocusAction] =
    for {
      toType <- caseClass.getCaseFieldType(caseFieldSymbol)
      setter <- constructSetter(caseFieldSymbol.name, caseClass.typeRepr, toType, caseClass.typeArgs)
    } yield FocusAction.SelectFieldWithImplicits(
      caseFieldSymbol,
      caseClass.typeRepr,
      toType,
      setter
    )

  private def getSelectOnlyFieldAction(
    caseClass: CaseClass,
    caseFieldSymbol: Symbol
  ): FocusResult[FocusAction] =
    for {
      toType <- caseClass.getCaseFieldType(caseFieldSymbol)
    } yield FocusAction.SelectOnlyField(
      caseFieldSymbol,
      caseClass.typeRepr,
      caseClass.typeArgs,
      caseClass.companionObject,
      toType
    )

  private def getSelectOnlyFieldActionWithImplicits(
    caseClass: CaseClass,
    caseFieldSymbol: Symbol
  ): FocusResult[FocusAction] =
    for {
      toType     <- caseClass.getCaseFieldType(caseFieldSymbol)
      reverseGet <- constructReverseGet(caseClass.companionObject, caseClass.typeRepr, toType, caseClass.typeArgs)
    } yield FocusAction.SelectOnlyFieldWithImplicits(
      caseFieldSymbol,
      caseClass.typeRepr,
      toType,
      reverseGet
    )

  private case class LiftException(error: FocusError) extends Exception

  private def liftEtaExpansionResult(term: => Term): FocusResult[Term] =
    scala.util.Try(term) match {
      case scala.util.Success(term)                 => Right(term)
      case scala.util.Failure(LiftException(error)) => Left(error)
      case scala.util.Failure(other)                => Left(FocusError.ExpansionFailed(other.toString))
    }

  private def constructSetter(
    fieldName: String,
    fromType: TypeRepr,
    toType: TypeRepr,
    fromTypeArgs: List[TypeRepr]
  ): FocusResult[Term] =
    // from.copy(value)(implicits)+
    (fromType.asType, toType.asType) match {
      case ('[f], '[t]) =>
        liftEtaExpansionResult('{ (to: t) => (from: f) =>
          ${
            etaExpandIfNecessary(
              Select.overloaded('{ from }.asTerm, "copy", fromTypeArgs, List(NamedArg(fieldName, '{ to }.asTerm)))
            ).fold(error => throw new LiftException(error), _.asExprOf[f])
          }
        }.asTerm)
    }

  private def constructReverseGet(
    companion: Term,
    fromType: TypeRepr,
    toType: TypeRepr,
    fromTypeArgs: List[TypeRepr]
  ): FocusResult[Term] =
    // Companion.apply(value)(implicits)+
    (fromType.asType, toType.asType) match {
      case ('[f], '[t]) =>
        liftEtaExpansionResult('{ (to: t) =>
          ${
            etaExpandIfNecessary(
              Select.overloaded(companion, "apply", fromTypeArgs, List('{ to }.asTerm))
            ).fold(error => throw new LiftException(error), _.asExprOf[f])
          }
        }.asTerm)
    }
}
