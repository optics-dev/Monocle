package monocle.internal.focus.features.withdefault

import monocle.internal.focus.FocusBase
import monocle.internal.focus.features.ParserBase

private[focus] trait WithDefaultParser {
  this: FocusBase with ParserBase =>

  object KeywordWithDefault extends FocusParser {

    def unapply(term: Term): Option[FocusResult[(RemainingCode, FocusAction)]] = term match {

      case FocusKeyword(Name("withDefault"), _, TypeArgs(toType), ValueArgs(defaultValue), remainingCode) => 
        val action = FocusAction.KeywordWithDefault(toType, defaultValue)
        Some(Right(remainingCode, action))

      case _ => None
    }
  }
}
