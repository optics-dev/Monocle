package monocle.internal.focus.features

import scala.quoted.Quotes
import monocle.internal.focus.FocusBase

private[focus] trait ParserBase {
  this: FocusBase => 

  import macroContext.reflect._

  // Marker class for type safety
  case class RemainingCode(code: Term)

  trait FocusParser {
    def unapply(term: Term): Option[FocusResult[(RemainingCode, FocusAction)]]
  }

  def getType(code: Term): TypeRepr = 
    code.tpe.widen.dealias

}