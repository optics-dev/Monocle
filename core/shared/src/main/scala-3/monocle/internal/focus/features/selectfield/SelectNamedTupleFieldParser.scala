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
                Apply(
                  TypeApply(Select(Ident("NamedTuple"), "apply") | Ident("apply"), List(namesTpe, valueTpes)),
                  remainingCode :: Nil
                ),
                Literal(IntConstant(idx)) :: Nil
              ) =>
            namedTuples.describe(getType(remainingCode)).flatMap { description =>
              val fieldType = description.values(idx)
              val fieldName = description.names(idx)
              Some(
                Right(
                  RemainingCode(remainingCode) -> FocusAction
                    .SelectNamedTupleField(fieldName, description, fieldType, namedTuples)
                )
              )
            }
          case _ => None
        }
      }
  }

}
