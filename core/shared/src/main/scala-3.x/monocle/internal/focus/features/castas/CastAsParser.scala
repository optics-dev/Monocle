package monocle.internal.focus.features.castas

import monocle.internal.focus.FocusBase
import monocle.internal.focus.features.ParserBase

private[focus] trait CastAsParser {
  this: FocusBase with ParserBase =>

  object CastAs extends FocusParser {

    def unapply(term: Term): Option[FocusResult[(RemainingCode, FocusAction)]] = term match {
      
      case FocusKeyword(Name("as"), FromType(fromType), TypeArgs(toType), remainingCode) => 
        if (toType <:< fromType) {
          val action = FocusAction.CastAs(fromType, toType)
          Some(Right(remainingCode, action))
        }
        else Some(Left(FocusError.InvalidDowncast(fromType.show, toType.show)))

      case _ => None
    }
  }
}
