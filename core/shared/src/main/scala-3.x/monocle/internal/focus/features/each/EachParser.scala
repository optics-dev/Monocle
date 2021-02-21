package monocle.internal.focus.features.each

import monocle.internal.focus.FocusBase
import monocle.internal.focus.features.ParserBase

private[focus] trait EachParser {
  this: FocusBase with ParserBase => 

  object Each extends FocusParser {

    def unapply(term: Term): Option[FocusResult[(RemainingCode, FocusAction)]] = term match {
      
      case FocusKeywordGiven(Name("each"), FromType(fromType), TypeArgs(_, toType), ValueArgs(), GivenInstance(eachInstance), remainingCode) => 
        val action = FocusAction.Each(fromType, toType, eachInstance)
        Some(Right(remainingCode, action))
        
      case _ => None
    }
  }
}