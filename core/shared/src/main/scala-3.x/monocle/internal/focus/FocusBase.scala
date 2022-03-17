package monocle.internal.focus

import scala.quoted.Quotes

private[focus] trait FocusBase {
  val macroContext: Quotes

  given Quotes = macroContext

  type Symbol   = macroContext.reflect.Symbol
  type Term     = macroContext.reflect.Term
  type TypeRepr = macroContext.reflect.TypeRepr

  case class LambdaConfig(argName: String, lambdaBody: Term)

  enum FocusAction {
    case SelectField(caseFieldSymbol: Symbol, fromType: TypeRepr, fromTypeArgs: List[TypeRepr], toType: TypeRepr)
    case SelectFieldWithImplicits(caseFieldSymbol: Symbol, fromType: TypeRepr, toType: TypeRepr, setter: Term)
    case SelectOnlyField(
      caseFieldSymbol: Symbol,
      fromType: TypeRepr,
      fromTypeArgs: List[TypeRepr],
      fromCompanion: Term,
      toType: TypeRepr
    )
    case SelectOnlyFieldWithImplicits(caseFieldSymbol: Symbol, fromType: TypeRepr, toType: TypeRepr, reverseGet: Term)
    case KeywordSome(toType: TypeRepr)
    case KeywordAs(fromType: TypeRepr, toType: TypeRepr)
    case KeywordEach(fromType: TypeRepr, toType: TypeRepr, eachInstance: Term)
    case KeywordAt(fromType: TypeRepr, toType: TypeRepr, index: Term, atInstance: Term)
    case KeywordIndex(fromType: TypeRepr, toType: TypeRepr, index: Term, indexInstance: Term)
    case KeywordWithDefault(toType: TypeRepr, defaultValue: Term)

    override def toString(): String = this match {
      case SelectField(caseFieldSymbol, fromType, fromTypeArgs, toType) =>
        s"SelectField(${caseFieldSymbol.name}, ${fromType.show}, ${fromTypeArgs.map(_.show)}, ${toType.show})"
      case SelectFieldWithImplicits(caseFieldSymbol, fromType, toType, setter) =>
        s"SelectFieldWithImplicits(${caseFieldSymbol.name}, ${fromType.show}, ${toType.show}, ...)"
      case SelectOnlyField(caseFieldSymbol, fromType, fromTypeArgs, _, toType) =>
        s"SelectOnlyField(${caseFieldSymbol.name}, ${fromType.show}, ${fromTypeArgs.map(_.show)}, ..., ${toType.show})"
      case SelectOnlyFieldWithImplicits(caseFieldSymbol, fromType, toType, reverseGet) =>
        s"SelectOnlyFieldWithImplicits(${caseFieldSymbol.name}, ${fromType.show}, ${toType.show}, ...)"
      case KeywordSome(toType)                  => s"KeywordSome(${toType.show})"
      case KeywordAs(fromType, toType)          => s"KeywordAs(${fromType.show}, ${toType.show})"
      case KeywordEach(fromType, toType, _)     => s"KeywordEach(${fromType.show}, ${toType.show}, ...)"
      case KeywordAt(fromType, toType, _, _)    => s"KeywordAt(${fromType.show}, ${toType.show}, ..., ...)"
      case KeywordIndex(fromType, toType, _, _) => s"KeywordIndex(${fromType.show}, ${toType.show}, ..., ...)"
      case KeywordWithDefault(toType, _)        => s"KeywordWithDefault(${toType.show}, ...)"
    }
  }

  enum FocusError {
    case NotACaseClass(className: String, fieldName: String)
    case NotACaseField(className: String, fieldName: String)
    case NonImplicitNonCaseParameter(className: String, parameters: List[String])
    case NotAConcreteClass(className: String)
    case DidNotDirectlyAccessArgument(argName: String)
    case NotASimpleLambdaFunction
    case CouldntUnderstandKeywordContext
    case UnexpectedCodeStructure(code: String)
    case CouldntFindFieldType(fromType: String, fieldName: String)
    case ComposeMismatch(type1: String, type2: String)
    case InvalidDowncast(fromType: String, toType: String)
    case ImplicitNotFound(implicitType: String, explanation: String)
    case ExpansionFailed(reason: String)

    def asResult: FocusResult[Nothing] = Left(this)
  }

  type FocusResult[+A] = Either[FocusError, A]
}
