package monocle.internal.focus.features

import monocle.internal.focus.FocusBase
import monocle.internal.focus.features.selectfield.SelectFieldGenerator
import monocle.internal.focus.features.selectonlyfield.SelectOnlyFieldGenerator
import monocle.internal.focus.features.some.SomeGenerator
import monocle.internal.focus.features.as.AsGenerator
import monocle.internal.focus.features.each.EachGenerator
import monocle.internal.focus.features.at.AtGenerator
import monocle.internal.focus.features.index.IndexGenerator
import monocle.internal.focus.features.withdefault.WithDefaultGenerator
import monocle.Iso
import scala.quoted.Type

private[focus] trait AllFeatureGenerators
    extends FocusBase
    with SelectFieldGenerator
    with SelectOnlyFieldGenerator
    with SomeGenerator
    with AsGenerator
    with EachGenerator
    with AtGenerator
    with IndexGenerator
    with WithDefaultGenerator

private[focus] trait GeneratorLoop {
  this: AllFeatureGenerators =>

  import macroContext.reflect._

  def generateCode[From: Type](actions: List[FocusAction]): FocusResult[Term] = {
    val idOptic: FocusResult[Term] = Right('{ Iso.id[From] }.asTerm)

    actions.foldLeft(idOptic) { (resultSoFar, action) =>
      resultSoFar.flatMap(term => composeOptics(term, generateActionCode(action)))
    }
  }

  private def generateActionCode(action: FocusAction): Term =
    action match {
      case a: FocusAction.SelectField        => generateSelectField(a)
      case a: FocusAction.SelectOnlyField    => generateSelectOnlyField(a)
      case a: FocusAction.KeywordSome        => generateSome(a)
      case a: FocusAction.KeywordAs          => generateAs(a)
      case a: FocusAction.KeywordEach        => generateEach(a)
      case a: FocusAction.KeywordAt          => generateAt(a)
      case a: FocusAction.KeywordIndex       => generateIndex(a)
      case a: FocusAction.KeywordWithDefault => generateWithDefault(a)
    }

  import scala.util.control.NonFatal

  private def composeOptics(lens1: Term, lens2: Term): FocusResult[Term] = {

    def dispatchType(tpe: TypeRepr): TypeRepr = tpe match {
      case AppliedType(tpe, _) => tpe
      case tpe                 => tpe
    }

    def erasedSubtypeOf(lhs: TypeRepr, rhs: TypeRepr): Boolean =
      rhs.classSymbol.map(lhs.derivesFrom).getOrElse(false)

    val tpe2 = dispatchType(lens2.tpe.dealias)

    lens1.tpe.classSymbol match {
      case Some(classSym) =>
        // This is to select the correct 'andThen' method. The algorithm
        // essentially loops through and finds the 'lowest' subtype which
        // is related to the term on the right.
        val methodSym = {
          var current: Null | (Symbol, TypeRepr) = null

          for {
            methodSym <- classSym.methodMember("andThen")
          } {
            val List(typeArgs, List(param)) = methodSym.paramSymss
            val ValDef(_, tpt, _)           = param.tree
            val tpe1                        = dispatchType(tpt.tpe)
            val replace = if (erasedSubtypeOf(tpe2, tpe1)) {
              current match {
                case null      => true
                case (_, tpe3) => erasedSubtypeOf(tpe1, tpe3)
              }
            } else {
              false
            }
            if (replace) {
              current = (methodSym, tpe1)
            }
          }
          current
        }

        methodSym match {
          case null => FocusError.ComposeMismatch(lens1.tpe.show, lens2.tpe.show).asResult
          case (methodSym, _) =>
            val AppliedType(_, List(_, toType2)) = lens2.tpe.widen
            val args                             = List.fill(methodSym.paramSymss.head.size)(toType2)
            try {
              val expr = Select(lens1, methodSym).appliedToTypes(args).appliedTo(lens2)
              Right(expr)
            } catch {
              case NonFatal(_) => FocusError.ComposeMismatch(lens1.tpe.show, lens2.tpe.show).asResult
            }
        }
      case None => FocusError.ComposeMismatch(lens1.tpe.show, lens2.tpe.show).asResult
    }

  }
}
