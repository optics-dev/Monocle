package monocle.internal.focus.features.selectfield

import monocle.internal.focus.FocusBase
import monocle.internal.focus.features.SelectParserBase
import scala.quoted.Type
import scala.quoted.Quotes
import scala.annotation.tailrec

private[focus] trait SelectNamedTupleFieldParser {
  this: FocusBase & SelectParserBase =>

  import this.macroContext.reflect.*

  object SelectNamedTupleField extends FocusParser {
    val namedTuples = NamedTuples.create

    def unapply(term: Term): Option[FocusResult[(RemainingCode, FocusAction)]] =
      namedTuples.flatMap { namedTuples =>
        term match {
          // TODO: document what kinda tree this actually matches (i.e. look at the desugared named tuple access calls)
          case Apply(
                Apply(
                  TypeApply(Select(Ident("NamedTuple"), "apply") | Ident("apply"), List(namesTpe, valueTpes)),
                  remainingCode :: Nil
                ),
                Literal(IntConstant(idx)) :: Nil
              ) =>
            namedTuples.describe(remainingCode.tpe).flatMap { description =>
              val fieldType = description.values(idx)
              val fieldName = description.names(idx)
              println(description)
              println()
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
