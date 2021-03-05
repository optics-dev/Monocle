package monocle.macros.syntax

import monocle.{Fold, Optional, Prism, Setter, Traversal}

import scala.reflect.macros.blackbox

trait MacroSyntax {
  implicit def toMacroPrismOps[S, A](optic: Prism[S, A]): MacroPrismOps[S, A]             = new MacroPrismOps(optic)
  implicit def toMacroOptionalOps[S, A](optic: Optional[S, A]): MacroOptionalOps[S, A]    = new MacroOptionalOps(optic)
  implicit def toMacroTraversalOps[S, A](optic: Traversal[S, A]): MacroTraversalOps[S, A] = new MacroTraversalOps(optic)
  implicit def toMacroSetterOps[S, A](optic: Setter[S, A]): MacroSetterOps[S, A]          = new MacroSetterOps(optic)
  implicit def toMacroFoldOps[S, A](optic: Fold[S, A]): MacroFoldOps[S, A]                = new MacroFoldOps(optic)
}

class MacroPrismOps[S, A](private val optic: Prism[S, A]) extends AnyVal {
  def as[CastTo <: A]: Prism[S, CastTo] = macro MacroAsOpsImpl.as_impl[Prism, S, A, CastTo]
}

class MacroOptionalOps[S, A](private val optic: Optional[S, A]) extends AnyVal {
  def as[CastTo <: A]: Optional[S, CastTo] = macro MacroAsOpsImpl.as_impl[Optional, S, A, CastTo]
}

class MacroTraversalOps[S, A](private val optic: Traversal[S, A]) extends AnyVal {
  def as[CastTo <: A]: Traversal[S, CastTo] = macro MacroAsOpsImpl.as_impl[Traversal, S, A, CastTo]
}

class MacroSetterOps[S, A](private val optic: Setter[S, A]) extends AnyVal {
  def as[CastTo <: A]: Setter[S, CastTo] = macro MacroAsOpsImpl.as_impl[Setter, S, A, CastTo]
}

class MacroFoldOps[S, A](private val optic: Fold[S, A]) extends AnyVal {
  def as[CastTo <: A]: Fold[S, CastTo] = macro MacroAsOpsImpl.as_impl[Fold, S, A, CastTo]
}

class MacroAsOpsImpl(val c: blackbox.Context) {
  def as_impl[Optic[_, _], From, To: c.WeakTypeTag, CastTo: c.WeakTypeTag]: c.Expr[Optic[From, CastTo]] = {
    import c.universe._

    val subj = c.prefix.tree match {
      case Apply(TypeApply(_, _), List(x)) => x
      case t                               => c.abort(c.enclosingPosition, s"Invalid prefix tree ${show(t)}")
    }

    c.Expr[Optic[From, CastTo]](
      q"""$subj.andThen(_root_.monocle.macros.GenPrism[${c.weakTypeOf[To]}, ${c.weakTypeOf[CastTo]}])"""
    )
  }
}
