package monocle.internal.focus.features.each

import monocle.internal.focus.FocusBase

private[focus] trait EachParser {
  this: FocusBase => 

  import macroContext.reflect._

  object Each extends FocusParser {

    def unapply(term: Term): Option[FocusResult[(Term, FocusAction)]] = term match {
      case Apply(Apply(TypeApply(Ident("each"), List(_, toTypeTree)), List(remainingCode)), List(eachInstance)) => 
        val fromType = remainingCode.tpe.widen
        val toType = toTypeTree.tpe
        val action = FocusAction.Each(fromType, toType, eachInstance)
        Some(Right(remainingCode, action))
        
      case _ => None
    }
  }
}