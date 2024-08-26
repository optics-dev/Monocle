package monocle.internal.focus.features.at

import monocle.internal.focus.FocusBase
import monocle.internal.focus.features.KeywordParserBase

private[focus] trait AtParser {
  this: FocusBase & KeywordParserBase =>

  object KeywordAt extends FocusParser {

    def unapply(term: Term): Option[FocusResult[(RemainingCode, FocusAction)]] = term match {

      case FocusKeywordGiven(
            Name("at"),
            FromType(fromType),
            TypeArgs(_, _, toType),
            ValueArgs(index),
            GivenInstance(atInstance),
            remainingCode
          ) =>
        val action = FocusAction.KeywordAt(fromType, toType, index, atInstance)
        Some(Right(remainingCode, action))

      case _ => None
    }
  }
}
