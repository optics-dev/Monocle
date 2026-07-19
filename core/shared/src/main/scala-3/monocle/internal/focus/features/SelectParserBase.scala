package monocle.internal.focus.features

import monocle.internal.focus.FocusBase
import scala.quoted.Type
import scala.quoted.Expr

private[focus] trait SelectParserBase extends ParserBase {
  this: FocusBase =>

  import this.macroContext.reflect.*

  // Match on a term that is an instance of a case class
  object CaseClass {
    def unapply(term: Term): Option[Term] =
      term.tpe.classSymbol.flatMap { sym =>
        Option.when(sym.flags.is(Flags.Case))(term)
      }
  }

  // unappliedNamedTuple is the type lambda [Names, Values] =>> NamedTuple[Names, Values], used to harvest its type symbol later on
  final class NamedTuples private (private val unappliedNamedTuple: Type[?], private val companion: Symbol) {
    def isNamedTuple(tpe: Type[?]) =
      TypeRepr.of(using tpe).dealias.typeSymbol == TypeRepr.of(using unappliedNamedTuple).typeSymbol

    // NamedTuple.toTuple[Names <: Tuple, Values <: Tuple](tup: NamedTuple.NamedTuple[Names, Values]): Values
    def toTuple(term: Term, description: NamedTuples.Description) =
      Select
        .unique(Ident(companion.termRef), "toTuple")
        .appliedToTypes(description.namesTpe :: description.valuesTpe :: Nil)
        .appliedTo(term)

    def accessFieldByName(term: Term, description: NamedTuples.Description, fieldName: String): Option[Term] = {
      val idxOfName = description.names.indexOf(fieldName)
      Option.when(idxOfName != -1) {
        val asTuple = toTuple(term, description)
        unsafeAccessFieldByIndex(asTuple, description.values, idxOfName)
      }
    }

    // there's a chance that we're operating on a non-normalized (non TupleN) tuple (for example when N is > 22 or when using NamedTuple.From)
    // in which case we need to fall back to using Product methods since TupleXXL <: scala.Product and doesn't get _N accessors
    private def unsafeAccessFieldByIndex(asTuple: Term, valueTpes: Vector[TypeRepr], index: Int) = {
      val tupleAccessor = s"_${index + 1}"

      if (asTuple.tpe.typeSymbol.fieldMember(tupleAccessor).exists) {
        Select.unique(asTuple, tupleAccessor)
      } else {
        val tpeAtIndex = valueTpes(index)
        (asTuple.asExpr, tpeAtIndex.asType) match {
          case '{ $prod: scala.Product } -> '[tpe] =>
            '{ $prod.productElement(${ Expr(index) }).asInstanceOf[tpe] }.asTerm
        }
      }
    }

    def reconstruct(from: Term, description: NamedTuples.Description, fieldToUpdate: String, updatedValue: Term) = {
      val updatedFieldIdx = description.names.indexOf(fieldToUpdate)
      val asTuple         = toTuple(from, description)
      val values          = 0
        .until(description.values.size)
        .map(idx =>
          if (idx == updatedFieldIdx) updatedValue.asExpr
          else unsafeAccessFieldByIndex(asTuple, description.values, idx).asExpr
        )
      Typed(Expr.ofTupleFromSeq(values).asTerm, TypeTree.of(using description.sourceType.asType))
    }
  }

  object NamedTuples {
    def create: Option[NamedTuples] = {
      val companion = Symbol.requiredModule("scala.NamedTuple")

      companion
        .declaredType("NamedTuple")
        .headOption
        .map(sym => NamedTuples(sym.typeRef.asType, companion))
    }

    case class Description(
      names: Vector[String],
      values: Vector[TypeRepr],
      sourceType: TypeRepr,
      namesTpe: TypeRepr,
      valuesTpe: TypeRepr
    )
  }

  def getSuppliedTypeArgs(fromType: TypeRepr): List[TypeRepr] =
    fromType match {
      case AppliedType(_, argTypeReprs) => argTypeReprs
      case _                            => Nil
    }

  def getClassSymbol(tpe: TypeRepr): FocusResult[Symbol] = tpe.classSymbol match {
    case Some(sym) => Right(sym)
    case None      => FocusError.NotAConcreteClass(tpe.show).asResult
  }

  private val tupleFieldPattern = "^_[0-9]+$".r

  def getFieldType(fromType: TypeRepr, fieldName: String, pos: Position): FocusResult[TypeRepr] = {
    def getFieldSymbol(fromTypeSymbol: Symbol): Symbol = {
      // We need to do this to support tuples, because even though they conform as case classes in other respects,
      // for some reason their field names (_1, _2, etc) have a space at the end, ie `_1 `.
      val f: String => String =
        if (fromType <:< TypeRepr.of[Tuple] && tupleFieldPattern.matches(fieldName))
          _.trim
        else
          identity
      fromTypeSymbol.fieldMembers.find(s => f(s.name) == fieldName).getOrElse(Symbol.noSymbol)
    }

    getClassSymbol(fromType).flatMap { fromTypeSymbol =>
      getFieldSymbol(fromTypeSymbol) match {
        case FieldType(possiblyTypeArg) => Right(swapWithSuppliedType(fromType, possiblyTypeArg))
        case _                          => FocusError.CouldntFindFieldType(fromType.show, fieldName, pos).asResult
      }
    }
  }

  private object FieldType {
    def unapply(fieldSymbol: Symbol): Option[TypeRepr] = fieldSymbol match {
      case sym if sym.isNoSymbol => None
      case sym                   =>
        sym.tree match {
          case ValDef(_, typeTree, _) => Some(typeTree.tpe)
          case _                      => None
        }
    }
  }

  private def swapWithSuppliedType(fromType: TypeRepr, possiblyContainsTypeArgs: TypeRepr): TypeRepr = {
    val declared = getDeclaredTypeArgs(fromType)
    val supplied = getSuppliedTypeArgs(fromType)
    val swapDict = declared.view.map(_.name).zip(supplied).toMap

    def swapInto(candidate: TypeRepr): TypeRepr =
      candidate match {
        case AppliedType(typeCons, args) => swapInto(typeCons).appliedTo(args.map(swapInto))
        case leafType                    => swapDict.getOrElse(leafType.typeSymbol.name, leafType)
      }
    swapInto(possiblyContainsTypeArgs)
  }

  private def getDeclaredTypeArgs(classType: TypeRepr): List[Symbol] =
    classType.classSymbol.map(_.primaryConstructor.paramSymss) match {
      case Some(typeParamList :: _) if typeParamList.exists(_.isTypeParam) => typeParamList
      case _                                                               => Nil
    }
}
