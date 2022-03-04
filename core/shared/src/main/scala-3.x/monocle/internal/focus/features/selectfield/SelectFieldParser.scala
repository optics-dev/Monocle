package monocle.internal.focus.features.selectfield

import monocle.internal.focus.FocusBase
import monocle.internal.focus.features.SelectParserBase

private[focus] trait SelectFieldParser {
  this: FocusBase with SelectParserBase =>

  import this.macroContext.reflect._

  object SelectField extends FocusParser {

    def unapply(term: Term): Option[FocusResult[(RemainingCode, FocusAction)]] = term match {

      case Select(CaseClass(remainingCode, classSymbol), fieldName) =>
        val fromType = getType(remainingCode)
        val action = if (hasOnlyOneParameterList(classSymbol)) {
          getFieldAction(fromType, fieldName)
        } else {
          getFieldActionWithImplicits(fromType, fieldName)
        }
        val remainingCodeWithAction = action.map(a => (RemainingCode(remainingCode), a))
        Some(remainingCodeWithAction)

      case Select(remainingCode, fieldName) =>
        Some(FocusError.NotACaseClass(remainingCode.tpe.show, fieldName).asResult)

      case _ => None
    }
  }

  private def hasOnlyOneParameterList(classSymbol: Symbol): Boolean =
    classSymbol.primaryConstructor.paramSymss match {
      case _ :: Nil                                    => true
      case (head :: _) :: _ :: Nil if head.isTypeParam => true
      case _                                           => false
    }

  private def getFieldAction(fromType: TypeRepr, fieldName: String): FocusResult[FocusAction] =
    getFieldType(fromType, fieldName).flatMap { toType =>
      Right(FocusAction.SelectField(fieldName, fromType, getSuppliedTypeArgs(fromType), toType))
    }

  private def getFieldActionWithImplicits(fromType: TypeRepr, fieldName: String): FocusResult[FocusAction] =
    getFieldType(fromType, fieldName).flatMap { toType =>
      val typeArgs = getSuppliedTypeArgs(fromType)
      constructSetter(fieldName, fromType, toType, typeArgs).map { setter =>
        FocusAction.SelectFieldWithImplicits(fieldName, fromType, toType, setter)
      }
    }

  private case class LiftException(error: FocusError) extends Exception

  private def constructSetter(
    fieldName: String,
    fromType: TypeRepr,
    toType: TypeRepr,
    fromTypeArgs: List[TypeRepr]
  ): FocusResult[Term] =
    // Companion.copy(value)(implicits)*
    (fromType.asType, toType.asType) match {
      case ('[f], '[t]) =>
        scala.util.Try('{ (to: t) => (from: f) =>
          ${
            etaExpandIfNecessary(
              Select.overloaded('{ from }.asTerm, "copy", fromTypeArgs, List(NamedArg(fieldName, '{ to }.asTerm)))
            ).fold(error => throw new LiftException(error), _.asExprOf[f])
          }
        }.asTerm) match {
          case scala.util.Success(term)                 => Right(term)
          case scala.util.Failure(LiftException(error)) => Left(error)
          case scala.util.Failure(other)                => Left(FocusError.ExpansionFailed(other.toString))
        }
    }
}
