package monocle.internal.focus.features.as

import monocle.internal.focus.FocusBase
import monocle.internal.focus.features.KeywordParserBase

private[focus] trait AsParser {
  this: FocusBase & KeywordParserBase =>

  object KeywordAs extends FocusParser {

    def unapply(term: Term): Option[FocusResult[(RemainingCode, FocusAction)]] = term match {

      case FocusKeyword(Name("as"), FromType(fromType), TypeArgs(_, toType), ValueArgs(), remainingCode) =>
        if (toType <:< fromType) {
          val action = FocusAction.KeywordAs(fromType, toType)
          Some(Right(remainingCode, action))
        } else Some(Left(FocusError.InvalidDowncast(fromType.show, toType.show)))

      case _ => None
    }
  }
}
