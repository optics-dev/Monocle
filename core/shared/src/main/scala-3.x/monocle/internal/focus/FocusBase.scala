package monocle.internal.focus

import scala.quoted.Quotes

private[focus] trait FocusBase {
  val macroContext: Quotes 

  given Quotes = macroContext

  type Term = macroContext.reflect.Term
  type TypeRepr = macroContext.reflect.TypeRepr

  case class LambdaConfig(argName: String, lambdaBody: Term)

  enum FocusAction {
    case FieldSelect(name: String, fromType: TypeRepr, fromTypeArgs: List[TypeRepr], toType: TypeRepr)
    case OptionSome(toType: TypeRepr)
    case CastAs(fromType: TypeRepr, toType: TypeRepr)
    case Each(fromType: TypeRepr, toType: TypeRepr, eachInstance: Term)

    override def toString(): String = this match {
      case FieldSelect(name, fromType, fromTypeArgs, toType) => s"FieldSelect($name, ${fromType.show}, ${fromTypeArgs.map(_.show)}, ${toType.show})"
      case OptionSome(toType) => s"OptionSome(${toType.show})"
      case CastAs(fromType, toType) => s"CastAs(${fromType.show}, ${toType.show})"
      case Each(fromType, toType, eachInstance) => s"Each(${fromType.show}, ${toType.show}, ...)"
    }
  }

  enum FocusError {
    case NotACaseClass(className: String, fieldName: String)
    case NotAConcreteClass(className: String)
    case DidNotDirectlyAccessArgument(argName: String)
    case NotASimpleLambdaFunction
    case CouldntRemoveMagicKeywords
    case UnexpectedCodeStructure(code: String)
    case CouldntFindFieldType(fromType: String, fieldName: String)
    case ComposeMismatch(type1: String, type2: String)
    case InvalidDowncast(fromType: String, toType: String)

    def asResult: FocusResult[Nothing] = Left(this)
  }

  trait FocusParser {
    def unapply(term: Term): Option[FocusResult[(Term, FocusAction)]]

    object FocusKeyword {
      import macroContext.reflect._

      def unapply(term: Term): Option[String] = term match {
        case Select(_, keyword) => Some(keyword)
        case _ => None
      }
    }
  }



  type FocusResult[+A] = Either[FocusError, A]
}