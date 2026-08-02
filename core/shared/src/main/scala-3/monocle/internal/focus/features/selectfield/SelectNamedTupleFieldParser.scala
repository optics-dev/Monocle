package monocle.internal.focus.features.selectfield

import monocle.internal.focus.FocusBase
import monocle.internal.focus.features.SelectParserBase

private[focus] trait SelectNamedTupleFieldParser {
  this: FocusBase & SelectParserBase =>

  import this.macroContext.reflect.*

  object SelectNamedTupleField extends FocusParser {

    def unapply(term: Term): Option[FocusResult[(RemainingCode, FocusAction)]] =
      NamedTuples.Support.flatMap { namedTuples =>
        term match {
          // the compiler expands a call like 'someNamedTuple.someField' to a call on NamedTuple.apply[Names, Values](someNamedTuple)(idx) where idx == index of 'someField' in the Names tuple
          case Apply(
                Apply(TypeApply(Select(ident, "apply"), _), remainingCode :: Nil),
                Literal(IntConstant(fieldIndex)) :: Nil
              ) if ident.symbol == namedTuples.companion =>
            for {
              description <- namedTuples.describe(getType(remainingCode))
              fieldType   <- description.values.lift(fieldIndex)
              fieldName   <- description.names.lift(fieldIndex)
              action = FocusAction.SelectNamedTupleField(fieldName, description, fieldType, namedTuples)
            } yield Right(RemainingCode(remainingCode) -> action)
          case _ => None
        }
      }
  }

}
