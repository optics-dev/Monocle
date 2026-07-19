package monocle.internal.focus.features.selectfield

import monocle.internal.focus.FocusBase
import monocle.internal.focus.features.SelectParserBase
import scala.quoted.Type
import scala.quoted.Quotes
import scala.annotation.tailrec

private[focus] trait SelectNamedTupleFieldParser {
  this: FocusBase & SelectParserBase =>

  import this.macroContext.reflect.*

  object SelectNamedTupleField extends FocusParser {

    def unapply(term: Term): Option[FocusResult[(RemainingCode, FocusAction)]] = term match {
      // TODO: document what kinda tree this actually matches (i.e. look at the desugared named tuple access calls)
      case Apply(
            Apply(
              TypeApply(Select(Ident("NamedTuple"), "apply") | Ident("apply"), List(namesTpe, valueTpes)),
              remainingCode :: Nil
            ),
            Literal(IntConstant(idx)) :: Nil
          ) =>
        val names     = unrollStrings(namesTpe.tpe)
        val fromType  = getType(remainingCode)
        val fieldType = unroll(valueTpes.tpe.asType)(idx)
        Some(Right(RemainingCode(remainingCode) -> FocusAction.SelectNamedTupleField(names(idx), fromType, fieldType)))

      case _ => None
    }
  }

  // TODO: handle errors with an either later on
  private def unrollStrings(tp: TypeRepr): Vector[String] =
    unroll(tp.asType).map { case ConstantType(StringConstant(l)) => l }

  private def unroll(tpe: Type[?]): Vector[TypeRepr] = {
    @tailrec def loop(curr: Type[?], acc: Vector[TypeRepr]): Vector[TypeRepr] =
      curr match {
        case '[head *: tail] =>
          loop(Type.of[tail], acc.appended(TypeRepr.of[head]))
        case '[EmptyTuple] =>
          acc
        case other =>
          report.errorAndAbort(
            s"Unexpected type (${Type.show(using other)}) encountered when extracting tuple type elems."
          )
      }

    loop(tpe, Vector.empty)
  }
}
