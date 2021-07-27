package monocle.internal.focus

import monocle.{Focus, Lens, Iso, Prism, Optional, Traversal, Getter, Setter, Fold, AppliedSetter, AppliedFold, AppliedGetter}
import scala.quoted.{Type, Expr, Quotes, quotes}

private[monocle] object ComposedFocusImpl {

  type AnyOptic[S,A] = Setter[S,A] | Fold[S,A] | AppliedSetter[S,A] | AppliedFold[S,A]

  def apply[S: Type, A: Type, Next: Type](optic: Expr[AnyOptic[S,A]], lambda: Expr[Focus.KeywordContext ?=> A => Next])(using Quotes): Expr[Any] = {
    import quotes.reflect._
    
    val generatedOptic = FocusImpl(lambda).asTerm
    val opticType = optic.asTerm.tpe.widen
    val nextType = TypeRepr.of[Next]
    val singleTypeParam: Boolean = 
      opticType =:= TypeRepr.of[Fold[S,A]] || 
      opticType =:= TypeRepr.of[Getter[S,A]] || 
      opticType =:= TypeRepr.of[AppliedFold[S,A]] || 
      opticType =:= TypeRepr.of[AppliedGetter[S,A]]

    val typeParams = if (singleTypeParam) List(nextType) else List(nextType, nextType)

    Select.overloaded(optic.asTerm, "andThen", typeParams, List(generatedOptic)).asExpr
  }
}