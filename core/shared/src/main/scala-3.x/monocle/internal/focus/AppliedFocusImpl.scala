package monocle.internal.focus

import monocle.{Focus, Lens, Iso, Prism, Optional}
import scala.quoted.{Type, Expr, Quotes, quotes}


private[monocle] object AppliedFocusImpl {
  def apply[From: Type, To: Type](from: Expr[From], lambda: Expr[InFocus ?=> From => To])(using Quotes): Expr[Any] = {
    import quotes.reflect._

    val generatedOptic = FocusImpl(lambda)

    generatedOptic.asTerm.tpe.asType match {
      case '[Lens[f, t]] => '{ _root_.monocle.syntax.ApplyLens[From, From, To, To]($from, ${generatedOptic.asExprOf[Lens[From,To]]}) }
      case '[Prism[f, t]] => '{ _root_.monocle.syntax.ApplyPrism[From, From, To, To]($from, ${generatedOptic.asExprOf[Prism[From,To]]}) }
      case '[Iso[f, t]] => '{ _root_.monocle.syntax.ApplyIso[From, From, To, To]($from, ${generatedOptic.asExprOf[Iso[From,To]]}) }
      case '[Optional[f, t]] => '{ _root_.monocle.syntax.ApplyOptional[From, From, To, To]($from, ${generatedOptic.asExprOf[Optional[From,To]]}) }
    }
  }
}
