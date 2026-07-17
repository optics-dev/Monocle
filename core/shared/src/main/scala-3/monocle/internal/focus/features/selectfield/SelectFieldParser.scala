package monocle.internal.focus.features.selectfield

import monocle.internal.focus.FocusBase
import monocle.internal.focus.features.SelectParserBase
import scala.quoted.Type
import scala.quoted.Quotes
import scala.annotation.tailrec

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

      // TODO: document what kinda tree this actually matches (i.e. look at the desugared named tuple access calls)
      case Apply(
            Apply(
              TypeApply(Select(Ident("NamedTuple"), "apply") | Ident("apply"), List(namesTpe, _)),
              remainingCode :: Nil
            ),
            Literal(IntConstant(idx)) :: Nil
          ) =>
        val names = unrollStrings(namesTpe.tpe)
        report.errorAndAbort(s"hit a named tuple access! Field: ${names(idx)}")
      // Logger.debug(s"Matching NamedTuple#apply at index ($idx)")
      // val names = TupleTypes.unrollStrings(namesTpe.tpe)
      // widen here because we're dealing with a singleton type of the lambda param, eg. '_$4'
      // recurse(acc.prepended(Path.Segment.Field(tree.tpe.widen.asType, names(idx))), tree)

      case Select(remainingCode, fieldName) =>
        Some(FocusError.NotACaseClass(remainingCode.tpe.widen.show, fieldName, term.pos).asResult)
      case _ => None
    }
  }

  private def getFieldAction(fromType: TypeRepr, fieldName: String, pos: Position): FocusResult[FocusAction] =
    getFieldType(fromType, fieldName, pos).flatMap { toType =>
      Right(FocusAction.SelectField(fieldName, fromType, getSuppliedTypeArgs(fromType), toType))
    }

  // TODO: unroll to something that has good index access
  private def unrollStrings(tp: TypeRepr): List[String] =
    unroll(tp.asType).map { case ConstantType(StringConstant(l)) => l }

  def unroll(tpe: Type[?]): List[TypeRepr] = {
    @tailrec def loop(curr: Type[?], acc: List[TypeRepr]): List[TypeRepr] =
      curr match {
        case '[head *: tail] =>
          loop(Type.of[tail], TypeRepr.of[head] :: acc)
        case '[EmptyTuple] =>
          acc
        case other =>
          report.errorAndAbort(
            s"Unexpected type (${Type.show(using other)}) encountered when extracting tuple type elems."
          )
      }

    loop(tpe, Nil).reverse
  }
}
