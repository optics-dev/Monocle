package monocle.internal.focus

import monocle.{Focus, Lens, Iso, Prism, Optional, Traversal, Getter, Setter, Fold}
import monocle.syntax.{AppliedPLens, AppliedPPrism, AppliedPIso, AppliedPOptional, AppliedPTraversal }
import scala.quoted.{Type, Expr, Quotes, quotes}


private[monocle] object ComposedFocusImpl {


  def apply[S: Type, A: Type,Next: Type](optic: Expr[Setter[S,A]], lambda: Expr[Focus.KeywordContext ?=> A => Next])(using Quotes): Expr[Any] = {
    import quotes.reflect._
    val generatedOptic = FocusImpl(lambda)

    generatedOptic.asTerm.tpe.asType match {
      case '[Lens[f, t]] => '{ ${optic}.andThen[Next, Next](${generatedOptic.asExprOf[Lens[A,Next]]}) }
      case '[Prism[f, t]] => '{ ${optic}.andThen[Next, Next](${generatedOptic.asExprOf[Prism[A,Next]]}) }
      case '[Iso[f, t]] => '{ ${optic}.andThen[Next, Next](${generatedOptic.asExprOf[Iso[A,Next]]}) }
      case '[Optional[f, t]] => '{ ${optic}.andThen[Next, Next](${generatedOptic.asExprOf[Optional[A,Next]]}) }
      case '[Traversal[f, t]] => '{ ${optic}.andThen[Next, Next](${generatedOptic.asExprOf[Traversal[A,Next]]}) }
    }
  }
}

