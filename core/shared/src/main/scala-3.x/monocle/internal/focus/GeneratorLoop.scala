package monocle.internal.focus

import monocle.internal.focus.features.fieldselect.FieldSelectGenerator
import monocle.internal.focus.features.optionsome.OptionSomeGenerator
import monocle.internal.focus.features.castas.CastAsGenerator
import monocle.internal.focus.features.each.EachGenerator
import monocle.{Lens, Iso, Prism, Optional, Traversal}
import scala.quoted.Type


private[focus] trait AllGenerators
  extends FocusBase
  with FieldSelectGenerator 
  with OptionSomeGenerator
  with CastAsGenerator
  with EachGenerator

private[focus] trait GeneratorLoop {
  this: FocusBase with AllGenerators => 

  import macroContext.reflect._

  def generateCode[From: Type](actions: List[FocusAction]): FocusResult[Term] = {
    val idOptic: FocusResult[Term] = Right('{Iso.id[From]}.asTerm)
    
    actions.foldLeft(idOptic) { (resultSoFar, action) => 
      resultSoFar.flatMap(term => composeOptics(term, generateActionCode(action)))
    }
  }

  private def generateActionCode(action: FocusAction): Term = 
    action match {
      case FocusAction.FieldSelect(name, fromType, fromTypeArgs, toType) => generateFieldSelect(name, fromType, fromTypeArgs, toType)
      case FocusAction.OptionSome(toType) => generateOptionSome(toType)
      case FocusAction.CastAs(fromType, toType) => generateCastAs(fromType, toType)
      case FocusAction.Each(fromType, toType, eachInstance) => generateEach(fromType, toType, eachInstance)
    }

  private def composeOptics(lens1: Term, lens2: Term): FocusResult[Term] = {
    lens2.tpe.widen match {
      // Won't yet work for polymorphism where A != B, nor for non-polymorphic optics Getter, Setter or Fold.
      case AppliedType(_, List(_, toType2)) => Right(Select.overloaded(lens1, "andThen", List(toType2, toType2), List(lens2)))
      case _ => FocusError.ComposeMismatch(lens1.tpe.show, lens2.tpe.show).asResult
    }
  }
}