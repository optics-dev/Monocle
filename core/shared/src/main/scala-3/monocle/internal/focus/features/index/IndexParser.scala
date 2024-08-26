package monocle.internal.focus.features.index

import monocle.internal.focus.FocusBase
import monocle.internal.focus.features.KeywordParserBase

private[focus] trait IndexParser {
  this: FocusBase & KeywordParserBase =>

  object KeywordIndex extends FocusParser {

    def unapply(term: Term): Option[FocusResult[(RemainingCode, FocusAction)]] = term match {

      case FocusKeywordGiven(
            Name("index"),
            FromType(fromType),
            TypeArgs(_, _, toType),
            ValueArgs(index),
            GivenInstance(indexInstance),
            remainingCode
          ) =>
        val action = FocusAction.KeywordIndex(fromType, toType, index, indexInstance)
        Some(Right(remainingCode, action))

      case _ => None
    }
  }
}
