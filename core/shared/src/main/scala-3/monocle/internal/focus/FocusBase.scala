package monocle.internal.focus

import scala.quoted.*
import scala.annotation.tailrec

private[focus] trait FocusBase {
  val macroContext: Quotes

  given Quotes = macroContext

  type Term     = macroContext.reflect.Term
  type TypeRepr = macroContext.reflect.TypeRepr
  type Position = macroContext.reflect.Position

  import macroContext.reflect.*

  case class LambdaConfig(argName: String, lambdaBody: Term)

  enum FocusAction {
    case SelectField(fieldName: String, fromType: TypeRepr, fromTypeArgs: List[TypeRepr], toType: TypeRepr)
    case SelectOnlyField(
      fieldName: String,
      fromType: TypeRepr,
      fromTypeArgs: List[TypeRepr],
      fromCompanion: Term,
      toType: TypeRepr
    )
    case SelectNamedTupleField(
      fieldName: String,
      fromDescription: NamedTuples.Description,
      toType: TypeRepr,
      namedTuples: NamedTuples
    )
    case KeywordSome(toType: TypeRepr)
    case KeywordAs(fromType: TypeRepr, toType: TypeRepr)
    case KeywordEach(fromType: TypeRepr, toType: TypeRepr, eachInstance: Term)
    case KeywordAt(fromType: TypeRepr, toType: TypeRepr, index: Term, atInstance: Term)
    case KeywordIndex(fromType: TypeRepr, toType: TypeRepr, index: Term, indexInstance: Term)
    case KeywordWithDefault(toType: TypeRepr, defaultValue: Term)

    override def toString(): String = this match {
      case SelectField(fieldName, fromType, fromTypeArgs, toType) =>
        s"SelectField($fieldName, ${fromType.show}, ${fromTypeArgs.map(_.show)}, ${toType.show})"
      case SelectOnlyField(fieldName, fromType, fromTypeArgs, _, toType) =>
        s"SelectOnlyField($fieldName, ${fromType.show}, ${fromTypeArgs.map(_.show)}, ..., ${toType.show})"
      case SelectNamedTupleField(fieldName, fromType, toType, _) =>
        s"SelectNamedTupleField($fieldName, ${fromType.show}, ${toType.show})"
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

  // unappliedNamedTuple is the type lambda [Names, Values] =>> NamedTuple[Names, Values], used to harvest its type symbol later on
  final class NamedTuples private (private val unappliedNamedTuple: TypeRepr, val companion: Symbol) {
    def isNamedTuple(tpe: TypeRepr) =
      tpe.dealias.typeSymbol == unappliedNamedTuple.typeSymbol

    // a call to NamedTuple.toTuple[Names <: Tuple, Values <: Tuple](tup: NamedTuple.NamedTuple[Names, Values]): Values
    def toTuple(term: Term, description: NamedTuples.Description) =
      Select
        .unique(Ident(companion.termRef), "toTuple")
        .appliedToTypes(description.namesTpe :: description.valuesTpe :: Nil)
        .appliedTo(term)

    def accessFieldByName(term: Term, action: FocusAction.SelectNamedTupleField): Term = {
      val idxOfName = action.fromDescription.names.indexOf(action.fieldName)
      val asTuple   = toTuple(term, action.fromDescription)
      unsafeAccessFieldByIndex(asTuple, action.fromDescription, idxOfName)
    }

    def reconstructWithUpdatedField(from: Term, action: FocusAction.SelectNamedTupleField, updatedValue: Term) = {
      val updatedFieldIdx = action.fromDescription.names.indexOf(action.fieldName)
      val asTuple         = toTuple(from, action.fromDescription)
      val values          =
        Vector.tabulate(action.fromDescription.values.size) { idx =>
          if (idx == updatedFieldIdx) updatedValue.asExpr
          else unsafeAccessFieldByIndex(asTuple, action.fromDescription, idx).asExpr
        }
      construct(action.fromDescription, values)
    }

    // NamedTuple >: Tuple so to 'construct' a named tuple we can just upcast an ordinary Tuple to a NamedTuple
    def construct(description: NamedTuples.Description, values: Seq[Expr[Any]]): Term =
      Typed(Expr.ofTupleFromSeq(values).asTerm, TypeTree.of(using description.sourceType.asType))

    // there's a chance that we're operating on a non-normalized (non TupleN) tuple (for example when N is > 22 or when using NamedTuple.From)
    // in which case we need to fall back to using Product methods since TupleXXL <: scala.Product and '*:' (tuple cons) <: Product AND doesn't get _N accessors
    private def unsafeAccessFieldByIndex(asTuple: Term, description: NamedTuples.Description, index: Int) = {
      val tupleAccessor = s"_${index + 1}"

      if (asTuple.tpe.typeSymbol.fieldMember(tupleAccessor).exists) {
        Select.unique(asTuple, tupleAccessor)
      } else {
        val tpeAtIndex = description.values(index)
        (asTuple.asExpr, tpeAtIndex.asType) match {
          case '{ $prod: scala.Product } -> '[tpe] =>
            '{ $prod.productElement(${ Expr(index) }).asInstanceOf[tpe] }.asTerm
        }
      }
    }

    def describe(sourceType: TypeRepr): Option[NamedTuples.Description] =
      sourceType.dealias.simplified match {
        case tpe @ AppliedType(_, namesTpe :: valuesTpe :: Nil) if isNamedTuple(tpe) =>
          Some(
            NamedTuples.Description(
              unrollStrings(namesTpe),
              unroll(valuesTpe),
              sourceType,
              namesTpe,
              valuesTpe
            )
          )
        case _ => None
      }

    private def unrollStrings(tp: TypeRepr): Vector[String] =
      unroll(tp).map { case ConstantType(StringConstant(l)) => l }

    private def unroll(tpe: TypeRepr): Vector[TypeRepr] = {
      @tailrec def loop(curr: Type[?], acc: Vector[TypeRepr]): Vector[TypeRepr] =
        curr match {
          case '[head *: tail] =>
            loop(Type.of[tail], acc.appended(TypeRepr.of[head]))
          case '[EmptyTuple] =>
            acc
        }

      loop(tpe.asType, Vector.empty)
    }

  }

  object NamedTuples {
    val Support: Option[NamedTuples] = {
      val companion = Symbol.requiredModule("scala.NamedTuple")

      companion
        .declaredType("NamedTuple")
        .headOption
        .map(sym => NamedTuples(sym.typeRef, companion))
    }

    case class Description private[NamedTuples] (
      names: Vector[String],
      values: Vector[TypeRepr],
      sourceType: TypeRepr,
      namesTpe: TypeRepr,
      valuesTpe: TypeRepr
    ) {
      def show: String =
        s"Description($names, ${values.map(_.show)}, ${sourceType.show}, ${namesTpe.show}, ${valuesTpe.show})"
    }
  }

}
