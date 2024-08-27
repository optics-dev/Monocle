package monocle.internal.focus

import monocle.{Focus, Iso, Lens, Optional, Prism, Traversal}
import monocle.syntax.{AppliedPIso, AppliedPLens, AppliedPOptional, AppliedPPrism, AppliedPTraversal}
import scala.quoted.{quotes, Expr, Quotes, Type}

private[monocle] object AppliedFocusImpl {
  def apply[From: Type, To: Type](from: Expr[From], lambda: Expr[Focus.KeywordContext ?=> From => To])(using
    Quotes
  ): Expr[Any] = {

    import quotes.reflect.*

    val generatedOptic = FocusImpl(lambda)

    generatedOptic.asTerm.tpe.asType match {
      case '[Lens[f, t]] => '{ AppliedPLens[From, From, To, To]($from, ${ generatedOptic.asExprOf[Lens[From, To]] }) }
      case '[Prism[f, t]] =>
        '{ AppliedPPrism[From, From, To, To]($from, ${ generatedOptic.asExprOf[Prism[From, To]] }) }
      case '[Iso[f, t]] => '{ AppliedPIso[From, From, To, To]($from, ${ generatedOptic.asExprOf[Iso[From, To]] }) }
      case '[Optional[f, t]] =>
        '{ AppliedPOptional[From, From, To, To]($from, ${ generatedOptic.asExprOf[Optional[From, To]] }) }
      case '[Traversal[f, t]] =>
        '{ AppliedPTraversal[From, From, To, To]($from, ${ generatedOptic.asExprOf[Traversal[From, To]] }) }
    }
  }
}
