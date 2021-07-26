package monocle.internal.focus

import monocle.{Focus, Lens, Iso, Prism, Optional, Traversal, Getter, Setter, Fold, AppliedSetter, AppliedFold, AppliedGetter}
import scala.quoted.{Type, Expr, Quotes, quotes}

private[monocle] object ComposedFocusImpl {
  def apply[S: Type, A: Type, Next: Type](optic: Expr[Setter[S,A] | Fold[S,A]], lambda: Expr[Focus.KeywordContext ?=> A => Next])(using Quotes): Expr[Any] = {
    import quotes.reflect._
    
    val generatedOptic = FocusImpl(lambda).asTerm
    val opticType = optic.asTerm.tpe.widen
    val nextType = TypeRepr.of[Next]
    
    def onlyRequiresOneTypeParameter(tpe: TypeRepr): Boolean = 
      tpe =:= TypeRepr.of[Fold[S,A]] || tpe =:= TypeRepr.of[Getter[S,A]]

    if (onlyRequiresOneTypeParameter(opticType)) {
      Select.overloaded(optic.asTerm, "andThen", List(nextType), List(generatedOptic)).asExpr
    } else {
      Select.overloaded(optic.asTerm, "andThen", List(nextType, nextType), List(generatedOptic)).asExpr
    }
  }

  def applied[S: Type, A: Type, Next: Type](optic: Expr[AppliedSetter[S,A] | AppliedFold[S,A]], lambda: Expr[Focus.KeywordContext ?=> A => Next])(using Quotes): Expr[Any] = {
    import quotes.reflect._
    
    val generatedOptic = FocusImpl(lambda).asTerm
    val opticType = optic.asTerm.tpe.widen
    val nextType = TypeRepr.of[Next]

    def onlyRequiresOneTypeParameter(tpe: TypeRepr): Boolean = 
      tpe =:= TypeRepr.of[AppliedFold[S,A]] || tpe =:= TypeRepr.of[AppliedGetter[S,A]]

    if (onlyRequiresOneTypeParameter(opticType)) {
      Select.overloaded(optic.asTerm, "andThen", List(nextType), List(generatedOptic)).asExpr
    } else {
      Select.overloaded(optic.asTerm, "andThen", List(nextType, nextType), List(generatedOptic)).asExpr
    }
  }
}