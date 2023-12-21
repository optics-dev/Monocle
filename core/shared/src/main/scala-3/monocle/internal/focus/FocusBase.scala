package monocle.internal.focus

import scala.quoted.Quotes

private[focus] trait FocusBase {
  val macroContext: Quotes

  given Quotes = macroContext

  type Term     = macroContext.reflect.Term
  type TypeRepr = macroContext.reflect.TypeRepr
  type Position = macroContext.reflect.Position

  case class LambdaConfig(argName: String, lambdaBody: Term)

  enum SelectType {
    case CaseClassField, PublicField, VirtualField
  }

  enum FocusAction {
    case SelectField(
      fieldName: String,
      fromType: TypeRepr,
      fromTypeArgs: List[TypeRepr],
      toType: TypeRepr,
      selectType: SelectType
    )
    case SelectOnlyField(
      fieldName: String,
      fromType: TypeRepr,
      fromTypeArgs: List[TypeRepr],
      fromCompanion: Term,
      toType: TypeRepr
    )
    case KeywordSome(toType: TypeRepr)
    case KeywordAs(fromType: TypeRepr, toType: TypeRepr)
    case KeywordEach(fromType: TypeRepr, toType: TypeRepr, eachInstance: Term)
    case KeywordAt(fromType: TypeRepr, toType: TypeRepr, index: Term, atInstance: Term)
    case KeywordIndex(fromType: TypeRepr, toType: TypeRepr, index: Term, indexInstance: Term)
    case KeywordWithDefault(toType: TypeRepr, defaultValue: Term)

    override def toString(): String = this match {
      case SelectField(fieldName, fromType, fromTypeArgs, toType, selectType) =>
        s"SelectField($fieldName, ${fromType.show}, ${fromTypeArgs.map(_.show)}, ${toType.show}, $selectType)"
      case SelectOnlyField(fieldName, fromType, fromTypeArgs, _, toType) =>
        s"SelectOnlyField($fieldName, ${fromType.show}, ${fromTypeArgs.map(_.show)}, ..., ${toType.show})"
      case KeywordSome(toType)                  => s"KeywordSome(${toType.show})"
      case KeywordAs(fromType, toType)          => s"KeywordAs(${fromType.show}, ${toType.show})"
      case KeywordEach(fromType, toType, _)     => s"KeywordEach(${fromType.show}, ${toType.show}, ...)"
      case KeywordAt(fromType, toType, _, _)    => s"KeywordAt(${fromType.show}, ${toType.show}, ..., ...)"
      case KeywordIndex(fromType, toType, _, _) => s"KeywordIndex(${fromType.show}, ${toType.show}, ..., ...)"
      case KeywordWithDefault(toType, _)        => s"KeywordWithDefault(${toType.show}, ...)"
    }
  }

  enum FocusError {
    case NotACaseClass(className: String, fieldName: String, pos: Position)
    case NotAConcreteClass(className: String)
    case DidNotDirectlyAccessArgument(argName: String)
    case NotASimpleLambdaFunction
    case CouldntUnderstandKeywordContext
    case UnexpectedCodeStructure(code: String)
    case CouldntFindFieldType(fromType: String, fieldName: String, pos: Position)
    case ComposeMismatch(type1: String, type2: String)
    case InvalidDowncast(fromType: String, toType: String)

    def asResult: FocusResult[Nothing] = Left(this)
  }

  type FocusResult[+A] = Either[FocusError, A]
}
