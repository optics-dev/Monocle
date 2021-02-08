package monocle.internal.focus

import monocle.internal.focus.features.fieldselect.FieldSelectGenerator
import monocle.internal.focus.features.optionsome.OptionSomeGenerator
import monocle.{Lens, Iso, Prism, Optional}
import scala.quoted.Type


private[focus] trait AllGenerators
  extends FocusBase
  with FieldSelectGenerator 
  with OptionSomeGenerator

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
    }

  private def composeOptics(lens1: Term, lens2: Term): FocusResult[Term] = {
    (lens1.tpe.asType, lens2.tpe.asType) match {
      case ('[Lens[from1, to1]], '[Lens[from2, to2]]) => 
        Right('{ ${lens1.asExprOf[Lens[from1, to1]]}.andThen(${lens2.asExprOf[Lens[to1, to2]]}) }.asTerm)

      case ('[Lens[from1, to1]], '[Prism[from2, to2]]) =>
        Right('{ ${lens1.asExprOf[Lens[from1, to1]]}.andThen(${lens2.asExprOf[Prism[to1, to2]]}) }.asTerm)

      case ('[Lens[from1, to1]], '[Optional[from2, to2]]) =>
        Right('{ ${lens1.asExprOf[Lens[from1, to1]]}.andThen(${lens2.asExprOf[Optional[to1, to2]]}) }.asTerm)

      case ('[Lens[from1, to1]], '[Iso[from2, to2]]) =>
        Right('{ ${lens1.asExprOf[Lens[from1, to1]]}.andThen(${lens2.asExprOf[Iso[to1, to2]]}) }.asTerm)

      case ('[Prism[from1, to1]], '[Prism[from2, to2]]) =>
        Right('{ ${lens1.asExprOf[Prism[from1, to1]]}.andThen(${lens2.asExprOf[Prism[to1, to2]]}) }.asTerm)

      case ('[Prism[from1, to1]], '[Lens[from2, to2]]) =>
        Right('{ ${lens1.asExprOf[Prism[from1, to1]]}.andThen(${lens2.asExprOf[Lens[to1, to2]]}) }.asTerm)

      case ('[Prism[from1, to1]], '[Optional[from2, to2]]) =>
        Right('{ ${lens1.asExprOf[Prism[from1, to1]]}.andThen(${lens2.asExprOf[Optional[to1, to2]]}) }.asTerm)

      case ('[Prism[from1, to1]], '[Iso[from2, to2]]) =>
        Right('{ ${lens1.asExprOf[Prism[from1, to1]]}.andThen(${lens2.asExprOf[Iso[to1, to2]]}) }.asTerm)

      case ('[Optional[from1, to1]], '[Lens[from2, to2]]) =>
        Right('{ ${lens1.asExprOf[Optional[from1, to1]]}.andThen(${lens2.asExprOf[Lens[to1, to2]]}) }.asTerm)

      case ('[Optional[from1, to1]], '[Optional[from2, to2]]) =>
        Right('{ ${lens1.asExprOf[Optional[from1, to1]]}.andThen(${lens2.asExprOf[Optional[to1, to2]]}) }.asTerm)

      case ('[Optional[from1, to1]], '[Prism[from2, to2]]) =>
        Right('{ ${lens1.asExprOf[Optional[from1, to1]]}.andThen(${lens2.asExprOf[Prism[to1, to2]]}) }.asTerm)

      case ('[Optional[from1, to1]], '[Iso[from2, to2]]) =>
        Right('{ ${lens1.asExprOf[Optional[from1, to1]]}.andThen(${lens2.asExprOf[Iso[to1, to2]]}) }.asTerm)

      case ('[Iso[from1, to1]], '[Lens[from2, to2]]) =>
        Right('{ ${lens1.asExprOf[Iso[from1, to1]]}.andThen(${lens2.asExprOf[Lens[to1, to2]]}) }.asTerm)

      case ('[Iso[from1, to1]], '[Iso[from2, to2]]) =>
        Right('{ ${lens1.asExprOf[Iso[from1, to1]]}.andThen(${lens2.asExprOf[Iso[to1, to2]]}) }.asTerm)

      case ('[Iso[from1, to1]], '[Optional[from2, to2]]) =>
        Right('{ ${lens1.asExprOf[Iso[from1, to1]]}.andThen(${lens2.asExprOf[Optional[to1, to2]]}) }.asTerm)

      case ('[Iso[from1, to1]], '[Prism[from2, to2]]) =>
        Right('{ ${lens1.asExprOf[Iso[from1, to1]]}.andThen(${lens2.asExprOf[Prism[to1, to2]]}) }.asTerm)

      case ('[a], '[b]) => 
        FocusError.ComposeMismatch(TypeRepr.of[a].show, TypeRepr.of[b].show).asResult
    }
  }
}