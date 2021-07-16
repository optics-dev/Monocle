package monocle.internal.focus

import monocle.{Focus, Lens, Iso, Prism, Optional, Traversal, Getter, Setter, Fold, AppliedSetter}
import scala.quoted.{Type, Expr, Quotes, quotes}


private[monocle] object ComposedFocusImpl {


  def apply[S: Type, A: Type, Next: Type](optic: Expr[Setter[S,A] | Fold[S,A]], lambda: Expr[Focus.KeywordContext ?=> A => Next])(using Quotes): Expr[Any] = {
    import quotes.reflect._
    val generatedOptic = FocusImpl(lambda)

    val typedGeneratedOptic = generatedOptic.asTerm.tpe.asType match {
      case '[Lens[f, t]] => generatedOptic.asExprOf[Lens[A,Next]]
      case '[Prism[f, t]] => generatedOptic.asExprOf[Prism[A,Next]]
      case '[Iso[f, t]] => generatedOptic.asExprOf[Iso[A,Next]]
      case '[Optional[f, t]] => generatedOptic.asExprOf[Optional[A,Next]]
      case '[Traversal[f, t]] => generatedOptic.asExprOf[Traversal[A,Next]]
    }


    '{
        ${optic} match {
          case setter: Setter[S,A] => setter.andThen[Next, Next](${generatedOptic.asExprOf[Setter[A, Next]]})
          case fold: Fold[S,A] => fold.andThen[Next](${generatedOptic.asExprOf[Fold[A, Next]]})
        }
    }

    /*
    generatedOptic.asTerm.tpe.asType match {
      case '[Lens[f, t]] => '{ ${setter}.andThen[Next, Next](${generatedOptic.asExprOf[Lens[A,Next]]}) }
      case '[Prism[f, t]] => '{ ${setter}.andThen[Next, Next](${generatedOptic.asExprOf[Prism[A,Next]]}) }
      case '[Iso[f, t]] => '{ ${setter}.andThen[Next, Next](${generatedOptic.asExprOf[Iso[A,Next]]}) }
      case '[Optional[f, t]] => '{ ${setter}.andThen[Next, Next](${generatedOptic.asExprOf[Optional[A,Next]]}) }
      case '[Traversal[f, t]] => '{ ${setter}.andThen[Next, Next](${generatedOptic.asExprOf[Traversal[A,Next]]}) }
    }*/
  }

  def onFold[S: Type, A: Type,Next: Type](optic: Expr[Fold[S,A]], lambda: Expr[Focus.KeywordContext ?=> A => Next])(using Quotes): Expr[Any] = {
    import quotes.reflect._
    val generatedOptic = FocusImpl(lambda)

    generatedOptic.asTerm.tpe.asType match {
      case '[Lens[f, t]] => '{ ${optic}.andThen[Next](${generatedOptic.asExprOf[Lens[A,Next]]}) }
      case '[Prism[f, t]] => '{ ${optic}.andThen[Next](${generatedOptic.asExprOf[Prism[A,Next]]}) }
      case '[Iso[f, t]] => '{ ${optic}.andThen[Next](${generatedOptic.asExprOf[Iso[A,Next]]}) }
      case '[Optional[f, t]] => '{ ${optic}.andThen[Next](${generatedOptic.asExprOf[Optional[A,Next]]}) }
      case '[Traversal[f, t]] => '{ ${optic}.andThen[Next](${generatedOptic.asExprOf[Traversal[A,Next]]}) }
    }
  }

  def applied[S: Type, A: Type,Next: Type](optic: Expr[AppliedSetter[S,A]], lambda: Expr[Focus.KeywordContext ?=> A => Next])(using Quotes): Expr[Any] = {
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

