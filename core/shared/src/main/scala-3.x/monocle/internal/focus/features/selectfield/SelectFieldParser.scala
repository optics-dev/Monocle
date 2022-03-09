package monocle.internal.focus.features.selectfield

import monocle.internal.focus.FocusBase
import monocle.internal.focus.features.SelectParserBase

private[focus] trait SelectFieldParser {
  this: FocusBase with SelectParserBase =>

  import this.macroContext.reflect._

  object SelectField extends FocusParser {

    def unapply(term: Term): Option[FocusResult[(RemainingCode, FocusAction)]] = term match {

      case Select(CaseClass(remainingCode, classSymbol), fieldName) =>
        if (isCaseField(classSymbol, fieldName)) {
          val fromType = getType(remainingCode)
          val action = (hasOnlyOneParameterList(classSymbol), hasOnlyOneField(classSymbol)) match {
            case (true, false)  => getSelectFieldAction(fromType, fieldName)
            case (false, false) => getSelectFieldActionWithImplicits(fromType, fieldName)
            case (true, true)   => getSelectOnlyFieldAction(fromType, classSymbol, fieldName)
            case (false, true)  => getSelectOnlyFieldActionWithImplicits(fromType, classSymbol, fieldName)
          }
          val remainingCodeWithAction = action.map(a => (RemainingCode(remainingCode), a))
          Some(remainingCodeWithAction)
        } else {
          Some(FocusError.NotACaseField(remainingCode.tpe.show, fieldName).asResult)
        }

      case Select(remainingCode, fieldName) =>
        Some(FocusError.NotACaseClass(remainingCode.tpe.show, fieldName).asResult)

      case _ => None
    }
  }

  private def isCaseField(classSymbol: Symbol, fieldName: String): Boolean =
    classSymbol.caseFields.exists(_.name == fieldName)

  private def hasOnlyOneField(classSymbol: Symbol): Boolean =
    classSymbol.caseFields.length == 1

  private def hasOnlyOneParameterList(classSymbol: Symbol): Boolean =
    classSymbol.primaryConstructor.paramSymss match {
      case _ :: Nil                                    => true
      case (head :: _) :: _ :: Nil if head.isTypeParam => true
      case _                                           => false
    }

  private def getCompanionObject(classSymbol: Symbol): Term =
    Ref(classSymbol.companionModule)

  private def getSelectFieldAction(fromType: TypeRepr, fieldName: String): FocusResult[FocusAction] =
    getFieldType(fromType, fieldName).flatMap { toType =>
      Right(FocusAction.SelectField(fieldName, fromType, getSuppliedTypeArgs(fromType), toType))
    }

  private def getSelectFieldActionWithImplicits(fromType: TypeRepr, fieldName: String): FocusResult[FocusAction] =
    getFieldType(fromType, fieldName).flatMap { toType =>
      val typeArgs = getSuppliedTypeArgs(fromType)
      constructSetter(fieldName, fromType, toType, typeArgs).map { setter =>
        FocusAction.SelectFieldWithImplicits(fieldName, fromType, toType, setter)
      }
    }

  private def getSelectOnlyFieldAction(
    fromType: TypeRepr,
    fromClassSymbol: Symbol,
    fieldName: String
  ): FocusResult[FocusAction] =
    for {
      toType <- getFieldType(fromType, fieldName)
      companion = getCompanionObject(fromClassSymbol)
      supplied  = getSuppliedTypeArgs(fromType)
    } yield FocusAction.SelectOnlyField(fieldName, fromType, supplied, companion, toType)

  private def getSelectOnlyFieldActionWithImplicits(
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
