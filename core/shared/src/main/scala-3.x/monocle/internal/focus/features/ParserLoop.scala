package monocle.internal.focus.features

import scala.quoted.Type
import monocle.internal.focus.FocusBase
import monocle.internal.focus.features.selectfield.SelectFieldParser
import monocle.internal.focus.features.selectonlyfield.SelectOnlyFieldParser
import monocle.internal.focus.features.selectsharedfield.SelectSharedFieldParser
import monocle.internal.focus.features.some.SomeParser
import monocle.internal.focus.features.as.AsParser
import monocle.internal.focus.features.each.EachParser
import monocle.internal.focus.features.at.AtParser
import monocle.internal.focus.features.index.IndexParser
import monocle.internal.focus.features.withdefault.WithDefaultParser

private[focus] trait AllFeatureParsers 
  extends FocusBase 
  with SelectParserBase
  with KeywordParserBase
  with SelectFieldParser 
  with SelectOnlyFieldParser
  with SelectSharedFieldParser
  with SomeParser
  with AsParser
  with EachParser
  with AtParser
  with IndexParser
  with WithDefaultParser

private[focus] trait ParserLoop {
  this: AllFeatureParsers => 

  import macroContext.reflect._

  def parseFocusActions(config: LambdaConfig): FocusResult[List[FocusAction]] = {
    def loop(remainingBody: RemainingCode, listSoFar: List[FocusAction]): FocusResult[List[FocusAction]] = {

      remainingBody.code match {
        case LambdaArgument(idName) if idName == config.argName => Right(listSoFar)
        case LambdaArgument(idName) => FocusError.DidNotDirectlyAccessArgument(idName).asResult

        case KeywordSome(Right(remainingCode, action)) => loop(remainingCode, action :: listSoFar)
        case KeywordSome(Left(error)) => Left(error)

        case KeywordEach(Right(remainingCode, action)) => loop(remainingCode, action :: listSoFar)
        case KeywordEach(Left(error)) => Left(error)

        case KeywordAt(Right(remainingCode, action)) => loop(remainingCode, action :: listSoFar)
        case KeywordAt(Left(error)) => Left(error)

        case KeywordIndex(Right(remainingCode, action)) => loop(remainingCode, action :: listSoFar)
        case KeywordIndex(Left(error)) => Left(error)

        case KeywordAs(Right(remainingCode, action)) => loop(remainingCode, action :: listSoFar)
        case KeywordAs(Left(error)) => Left(error)

        case KeywordWithDefault(Right(remainingCode, action)) => loop(remainingCode, action :: listSoFar)
        case KeywordWithDefault(Left(error)) => Left(error)

        case SelectSharedField(Right(remainingCode, action)) => loop(remainingCode, action :: listSoFar)
        case SelectSharedField(Left(error)) => Left(error)

        case SelectOnlyField(Right(remainingCode, action)) => loop(remainingCode, action :: listSoFar)
        case SelectOnlyField(Left(error)) => Left(error)

        case SelectField(Right(remainingCode, action)) => loop(remainingCode, action :: listSoFar)
        case SelectField(Left(error)) => Left(error)

        case unexpected => FocusError.UnexpectedCodeStructure(unexpected.toString).asResult
      }
    }
    loop(RemainingCode(config.lambdaBody), Nil)
  }

  private object LambdaArgument {
    def unapply(term: Term): Option[String] = term match {
      case Ident(idName) => Some(idName)
      case _ => None
    }
  }

}