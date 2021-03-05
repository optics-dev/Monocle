package monocle.internal.focus.features.each

import monocle.internal.focus.FocusBase
import monocle.internal.focus.features.KeywordParserBase

private[focus] trait EachParser {
  this: FocusBase with KeywordParserBase => 

  object KeywordEach extends FocusParser {

    def unapply(term: Term): Option[FocusResult[(RemainingCode, FocusAction)]] = term match {
      
      case FocusKeywordGiven(Name("each"), FromType(fromType), TypeArgs(_, toType), ValueArgs(), GivenInstance(eachInstance), remainingCode) => 
        val action = FocusAction.KeywordEach(fromType, toType, eachInstance)
        Some(Right(remainingCode, action))
        
      case _ => None
    }
  }
}