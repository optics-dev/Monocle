package monocle.internal.focus

import monocle.{Focus, Lens, Iso, Prism, Optional, Traversal }
import monocle.syntax.{ApplyLens, ApplyPrism, ApplyIso, ApplyOptional, ApplyTraversal }
import scala.quoted.{Type, Expr, Quotes, quotes}


private[monocle] object AppliedFocusImpl {
  def apply[From: Type, To: Type](from: Expr[From], lambda: Expr[Focus.MagicKeywords ?=> From => To])(using Quotes): Expr[Any] = {

    import quotes.reflect._

    val generatedOptic = FocusImpl(lambda)

    generatedOptic.asTerm.tpe.asType match {
      case '[Lens[f, t]] => '{ ApplyLens[From, From, To, To]($from, ${generatedOptic.asExprOf[Lens[From,To]]}) }
      case '[Prism[f, t]] => '{ ApplyPrism[From, From, To, To]($from, ${generatedOptic.asExprOf[Prism[From,To]]}) }
      case '[Iso[f, t]] => '{ ApplyIso[From, From, To, To]($from, ${generatedOptic.asExprOf[Iso[From,To]]}) }
      case '[Optional[f, t]] => '{ ApplyOptional[From, From, To, To]($from, ${generatedOptic.asExprOf[Optional[From,To]]}) }
      case '[Traversal[f, t]] => '{ ApplyTraversal[From, From, To, To]($from, ${generatedOptic.asExprOf[Traversal[From,To]]}) }
    }
  }
}
