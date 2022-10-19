package monocle.internal.focus.features

import monocle.internal.focus.FocusBase
import scala.annotation.tailrec
import scala.util.Failure
import scala.util.Success
import scala.util.Try

private[focus] trait SelectParserBase extends ParserBase {
  this: FocusBase =>

  import this.macroContext.reflect._

  case class CaseClass(typeRepr: TypeRepr, classSymbol: Symbol) {
    val typeArgs: List[TypeRepr] = getSuppliedTypeArgs(typeRepr)
    val companionObject: Term    = Ref(classSymbol.companionModule)

    private val (typeParams, caseFieldParams :: otherParams) =
      classSymbol.primaryConstructor.paramSymss.span(_.headOption.fold(false)(_.isTypeParam))
    val hasOnlyOneCaseField: Boolean     = caseFieldParams.length == 1
    val hasOnlyOneParameterList: Boolean = otherParams.isEmpty
    private val nonCaseNonImplicitParameters: List[Symbol] =
      otherParams.flatten.filterNot(symbol => symbol.flags.is(Flags.Implicit) || symbol.flags.is(Flags.Given))
    val allOtherParametersAreImplicitResult: FocusResult[Unit] = nonCaseNonImplicitParameters match {
      case Nil  => Right(())
      case list => FocusError.NonImplicitNonCaseParameter(typeRepr.show, list.map(_.name)).asResult
    }

    def getCaseFieldSymbol(fieldName: String): FocusResult[Symbol] =
      classSymbol.caseFields.find(_.name == fieldName) match {
        case Some(symbol) => Right(symbol)
        case None         => FocusError.NotACaseField(typeRepr.show, fieldName).asResult
      }
    def getCaseFieldType(caseFieldSymbol: Symbol): FocusResult[TypeRepr] =
      caseFieldSymbol match {
        case FieldType(possiblyTypeArg) => Right(swapWithSuppliedType(typeRepr, possiblyTypeArg))
        case _                          => FocusError.CouldntFindFieldType(typeRepr.show, caseFieldSymbol.name).asResult
      }
  }

  object CaseClassExtractor {
    def unapply(term: Term): Option[CaseClass] =
      term.tpe.classSymbol.flatMap { sym =>
        Option.when(sym.flags.is(Flags.Case))(CaseClass(getType(term), sym))
      }
  }

  private def getSuppliedTypeArgs(fromType: TypeRepr): List[TypeRepr] =
    fromType match {
      case AppliedType(_, argTypeReprs) => argTypeReprs
      case _                            => Nil
    }

  private object FieldType {
    def unapply(fieldSymbol: Symbol): Option[TypeRepr] = fieldSymbol match {
      case sym if sym.isNoSymbol => None
      case sym =>
        sym.tree match {
          case ValDef(_, typeTree, _) => Some(typeTree.tpe)
          // Only needed for Tuples because `_1` is a DefDef while `_1 ` is the corresponding ValDef.
          case DefDef(_, _, typeTree, _) => Some(typeTree.tpe)
          case _                         => None
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

  @tailrec
  final def etaExpandIfNecessary(term: Term): FocusResult[Term] =
    if (term.isExpr) {
      Right(term)
    } else {
      val expanded: Term = term.etaExpand(Symbol.spliceOwner)

      val implicitsResult: FocusResult[List[Term]] =
        expanded match {
          case Block(List(DefDef(_, List(params), _, _)), _) =>
            params.params.foldLeft[FocusResult[List[Term]]](Right(List.empty[Term])) {
              case (Right(acc), ValDef(_, t, _)) =>
                def searchForImplicit(typeRepr: TypeRepr): FocusResult[Term] =
                  Implicits.search(typeRepr) match {
                    case success: ImplicitSearchSuccess =>
                      Right(success.tree)
                    case failure: ImplicitSearchFailure =>
                      FocusError.ImplicitNotFound(typeRepr.show, failure.explanation).asResult
                  }

                searchForImplicit(t.tpe)
                  .map(acc :+ _)

              case (Right(acc), other) =>
                FocusError.ExpansionFailed(s"Expected value definition but found unexpected ${other.show}").asResult
              case (left @ Left(_), _) =>
                left
            }
          case other =>
            FocusError.ExpansionFailed(s"Expected block of expanded term but found unexpected ${other.show}").asResult
        }

      implicitsResult match {
        case Left(error)      => Left(error)
        case Right(implicits) => etaExpandIfNecessary(Apply(term, implicits))
      }
    }
}
