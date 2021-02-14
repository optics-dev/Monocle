package monocle.internal.focus

import monocle.syntax.FocusSyntax
import scala.quoted.{Quotes, Type, Expr}
import scala.annotation.implicitNotFound

private[focus] object InFocus {
  def stripContext[From : Type, To: Type](
    contextExpr: Expr[FocusSyntax ?=> From => To]
  )(using macroContext: Quotes): Option[Expr[From => To]] = {
    import macroContext.reflect._
    // Ideally, a quasi-quote match would be best, but it doesn't work currently for certain cases.
    contextExpr.asTerm match {
      case Inlined(_, _, Block(List(DefDef(_, _, _, _, Some(lambdaExpr))), _)) =>
        Some(lambdaExpr.asExprOf[From => To])
      case _ => None
    }
  }
}
