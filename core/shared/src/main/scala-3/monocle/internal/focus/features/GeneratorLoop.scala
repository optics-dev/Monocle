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
import monocle.{Iso, Lens, Optional, Prism, Traversal}
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

  private def composeOptics(lens1: Term, lens2: Term): FocusResult[Term] =
    lens2.tpe.widen match {
      // Won't yet work for polymorphism where A != B, nor for non-polymorphic optics Getter or Fold.
      case AppliedType(_, List(_, toType2)) =>
        Right(Select.overloaded(lens1, "andThen", List(toType2, toType2), List(lens2)))
      case _ => FocusError.ComposeMismatch(lens1.tpe.show, lens2.tpe.show).asResult
    }
}
