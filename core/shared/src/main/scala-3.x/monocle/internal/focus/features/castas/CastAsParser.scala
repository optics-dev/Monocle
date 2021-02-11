package monocle.internal.focus.features.castas

import monocle.internal.focus.FocusBase

private[focus] trait CastAsParser {
  this: FocusBase => 

  import macroContext.reflect._

  object CastAs extends FocusParser {

    def unapply(term: Term): Option[FocusResult[(Term, FocusAction)]] = term match {
      case Apply(TypeApply(Ident("as"), List(typeArg)), List(remainingCode)) => 
        val toType = typeArg.tpe
        val fromType = remainingCode.tpe.widen
        val action = FocusAction.CastAs(fromType, toType)
        Some(Right(remainingCode, action))
        
      case _ => None
    }
  }
}