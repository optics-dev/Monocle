package monocle.internal.focus.features

import scala.quoted.Quotes
import monocle.internal.focus.FocusBase

private[focus] trait ParserBase {
  this: FocusBase =>

  import macroContext.reflect.*

  // Marker class for type safety
  case class RemainingCode(code: Term)

  trait FocusParser {
    def unapply(term: Term): Option[FocusResult[(RemainingCode, FocusAction)]]
  }

  // the '.simplified' call here is needed because otherwise if an unreduced match type arrives at this call site we're greeted with a compiler barf, like:
  // 'Cannot get type of value [...]' (note that this is especially important for terms that describe a named tuple field access which is typed as 'Elem[NamedTuple[N, V], n.type]' which IS a match type).
  def getType(code: Term): TypeRepr =
    code.tpe.widen.dealias.simplified

}
