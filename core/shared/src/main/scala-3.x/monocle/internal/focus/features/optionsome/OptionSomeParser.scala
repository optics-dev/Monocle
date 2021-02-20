package monocle.internal.focus.features.optionsome

import monocle.internal.focus.FocusBase
import monocle.internal.focus.features.ParserBase

private[focus] trait OptionSomeParser {
  this: FocusBase with ParserBase =>

  object OptionSome extends FocusParser {

    def unapply(term: Term): Option[FocusResult[(RemainingCode, FocusAction)]] = term match {

      case FocusKeyword(Name("some"), _, TypeArgs(toType), remainingCode) => 
        val action = FocusAction.OptionSome(toType)
        Some(Right(remainingCode, action))

      case _ => None
    }
  }
}
