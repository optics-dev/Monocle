package monocle.internal.focus.features.selectfield

import monocle.internal.focus.FocusBase
import monocle.internal.focus.features.SelectParserBase
import scala.quoted.Type
import scala.quoted.Quotes

private[focus] trait SelectFieldParser {
  this: FocusBase & SelectParserBase =>

  import this.macroContext.reflect.*

  object SelectField extends FocusParser {

    def unapply(term: Term): Option[FocusResult[(RemainingCode, FocusAction)]] = term match {

      case Select(CaseClass(remainingCode), fieldName) =>
        val fromType                = getType(remainingCode)
        val action                  = getFieldAction(fromType, fieldName, term.pos)
        val remainingCodeWithAction = action.map(a => (RemainingCode(remainingCode), a))
        Some(remainingCodeWithAction)

      case Select(remainingCode, fieldName) =>
        Some(FocusError.NotACaseClass(remainingCode.tpe.widen.show, fieldName, term.pos).asResult)
      case _ => None
    }
  }

  private def getFieldAction(fromType: TypeRepr, fieldName: String, pos: Position): FocusResult[FocusAction] =
    getFieldType(fromType, fieldName, pos).flatMap { toType =>
      Right(FocusAction.SelectField(fieldName, fromType, getSuppliedTypeArgs(fromType), toType))
    }
  // unappliedNamedTuple is the type lambda [Names, Values] =>> NamedTuple[Names, Values], used to harvest its type symbol later on
  final class NamedTuples private (private val unappliedNamedTuple: Type[?]) {
    def isNamedTuple(tpe: Type[?]) =
      TypeRepr.of(using tpe).dealias.typeSymbol == TypeRepr.of(using unappliedNamedTuple).typeSymbol
  }

  object NamedTuples {
    def create: Option[NamedTuples] =
      Symbol
        .requiredModule("scala.NamedTuple")
        .declaredType("NamedTuple")
        .headOption
        .map(sym => NamedTuples(sym.typeRef.asType))
  }
}
