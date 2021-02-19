package monocle.internal.focus

import scala.quoted.Type
import monocle.internal.focus.features.fieldselect.FieldSelectParser
import monocle.internal.focus.features.optionsome.OptionSomeParser
import monocle.internal.focus.features.castas.CastAsParser
import monocle.internal.focus.features.each.EachParser

private[focus] trait AllFeatureParsers 
  extends FocusBase
  with FieldSelectParser 
  with OptionSomeParser
  with CastAsParser
  with EachParser

private[focus] trait ParserLoop {
  this: FocusBase with AllFeatureParsers => 

  import macroContext.reflect._

  def parseFocusActions(config: LambdaConfig): FocusResult[List[FocusAction]] = {
    def loop(remainingBody: Term, listSoFar: List[FocusAction]): FocusResult[List[FocusAction]] = {

      remainingBody match {
        case LambdaArgument(idName) if idName == config.argName => Right(listSoFar)
        case LambdaArgument(idName) => FocusError.DidNotDirectlyAccessArgument(idName).asResult

        case OptionSome(Right(remainingCode, action)) => loop(remainingCode, action :: listSoFar)
        case OptionSome(Left(error)) => Left(error)

        case Each(Right(remainingCode, action)) => loop(remainingCode, action :: listSoFar)
        case Each(Left(error)) => Left(error)

        case CastAs(Right(remainingCode, action)) => loop(remainingCode, action :: listSoFar)
        case CastAs(Left(error)) => Left(error)

        case FieldSelect(Right(remainingCode, action)) => loop(remainingCode, action :: listSoFar)
        case FieldSelect(Left(error)) => Left(error)

        case unexpected => FocusError.UnexpectedCodeStructure(unexpected.toString).asResult
      }
    }
    loop(config.lambdaBody, Nil)
  }

  private object LambdaArgument {
    def unapply(term: Term): Option[String] = term match {
      case Ident(idName) => Some(idName)
      case _ => None
    }
  }

}