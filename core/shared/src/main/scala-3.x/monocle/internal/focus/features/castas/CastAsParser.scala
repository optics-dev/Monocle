package monocle.internal.focus.features.castas

import monocle.internal.focus.FocusBase

private[focus] trait CastAsParser {
  this: FocusBase =>

  import macroContext.reflect._

  object CastAs extends FocusParser {

    def unapply(term: Term): Option[FocusResult[(Term, FocusAction)]] = term match {
      case Apply(TypeApply(Select(_, "as"), List(typeArg)), List(remainingCode)) =>

        val fromType = remainingCode.tpe.widen
        val toType = typeArg.tpe


        if (toType <:< fromType) {
          val action = FocusAction.CastAs(fromType, toType)
          Some(Right(remainingCode, action))
        }
        else Some(Left(FocusError.InvalidDowncast(fromType.show, toType.show)))

      case _ => None
    }
  }
}
