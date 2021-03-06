package monocle.internal.focus

import monocle.{Focus, Lens, Iso, Prism, Optional, Traversal }
import monocle.syntax.{ApplyPLens, ApplyPPrism, ApplyPIso, ApplyPOptional, ApplyPTraversal }
import scala.quoted.{Type, Expr, Quotes, quotes}


private[monocle] object AppliedFocusImpl {
  def apply[From: Type, To: Type](from: Expr[From], lambda: Expr[Focus.KeywordContext ?=> From => To])(using Quotes): Expr[Any] = {

    import quotes.reflect._

    val generatedOptic = FocusImpl(lambda)

    generatedOptic.asTerm.tpe.asType match {
      case '[Lens[f, t]] => '{ ApplyPLens[From, From, To, To]($from, ${generatedOptic.asExprOf[Lens[From,To]]}) }
      case '[Prism[f, t]] => '{ ApplyPPrism[From, From, To, To]($from, ${generatedOptic.asExprOf[Prism[From,To]]}) }
      case '[Iso[f, t]] => '{ ApplyPIso[From, From, To, To]($from, ${generatedOptic.asExprOf[Iso[From,To]]}) }
      case '[Optional[f, t]] => '{ ApplyPOptional[From, From, To, To]($from, ${generatedOptic.asExprOf[Optional[From,To]]}) }
      case '[Traversal[f, t]] => '{ ApplyPTraversal[From, From, To, To]($from, ${generatedOptic.asExprOf[Traversal[From,To]]}) }
    }
  }
}
