package monocle.internal.focus.features.some

import monocle.internal.focus.FocusBase
import monocle.internal.focus.features.KeywordParserBase

private[focus] trait SomeParser {
  this: FocusBase with KeywordParserBase =>

  object KeywordSome extends FocusParser {

    def unapply(term: Term): Option[FocusResult[(RemainingCode, FocusAction)]] = term match {

      case FocusKeyword(Name("some"), _, TypeArgs(toType), ValueArgs(), remainingCode) =>
        val action = FocusAction.KeywordSome(toType)
        Some(Right(remainingCode, action))

      case _ => None
    }
  }
}
