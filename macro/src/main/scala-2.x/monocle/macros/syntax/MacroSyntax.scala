package monocle.macros.syntax

import monocle._

import scala.reflect.macros.blackbox

trait MacroSyntax {
  implicit def toMacroLensOps[S, A](optic: Lens[S, A]): MacroLensOps[S, A]                = new MacroLensOps(optic)
  implicit def toMacroPrismOps[S, A](optic: Prism[S, A]): MacroPrismOps[S, A]             = new MacroPrismOps(optic)
  implicit def toMacroOptionalOps[S, A](optic: Optional[S, A]): MacroOptionalOps[S, A]    = new MacroOptionalOps(optic)
  implicit def toMacroTraversalOps[S, A](optic: Traversal[S, A]): MacroTraversalOps[S, A] = new MacroTraversalOps(optic)
  implicit def toMacroSetterOps[S, A](optic: Setter[S, A]): MacroSetterOps[S, A]          = new MacroSetterOps(optic)
  implicit def toMacroGetterOps[S, A](optic: Getter[S, A]): MacroGetterOps[S, A]          = new MacroGetterOps(optic)
  implicit def toMacroFoldOps[S, A](optic: Fold[S, A]): MacroFoldOps[S, A]                = new MacroFoldOps(optic)

  implicit def toMacroLensOps[S, A](optic: AppliedLens[S, A]): MacroAppliedLensOps[S, A] =
    new MacroAppliedLensOps(optic)
  implicit def toMacroAppliedPrismOps[S, A](optic: AppliedPrism[S, A]): MacroAppliedPrismOps[S, A] =
    new MacroAppliedPrismOps(optic)
  implicit def toMacroAppliedOptionalOps[S, A](optic: AppliedOptional[S, A]): MacroAppliedOptionalOps[S, A] =
    new MacroAppliedOptionalOps(optic)
  implicit def toMacroAppliedTraversalOps[S, A](optic: AppliedTraversal[S, A]): MacroAppliedTraversalOps[S, A] =
    new MacroAppliedTraversalOps(optic)
  implicit def toMacroAppliedSetterOps[S, A](optic: AppliedSetter[S, A]): MacroAppliedSetterOps[S, A] =
    new MacroAppliedSetterOps(optic)
  implicit def toMacroAppliedGetterOps[S, A](optic: AppliedGetter[S, A]): MacroAppliedGetterOps[S, A] =
    new MacroAppliedGetterOps(optic)
  implicit def toMacroAppliedFoldOps[S, A](optic: AppliedFold[S, A]): MacroAppliedFoldOps[S, A] =
    new MacroAppliedFoldOps(optic)
}

class MacroLensOps[S, A](private val optic: Lens[S, A]) extends AnyVal {
  def refocus[Next](lambda: A => Next): Lens[S, Next] = macro MacroOpsImpl.refocus_impl[Lens, S, A, Next]
}

class MacroPrismOps[S, A](private val optic: Prism[S, A]) extends AnyVal {
  def as[CastTo <: A]: Prism[S, CastTo] = macro MacroOpsImpl.as_impl[Prism, S, A, CastTo]
}

class MacroOptionalOps[S, A](private val optic: Optional[S, A]) extends AnyVal {
  def refocus[Next](lambda: A => Next): Optional[S, Next] = macro MacroOpsImpl.refocus_impl[Optional, S, A, Next]
  def as[CastTo <: A]: Optional[S, CastTo] = macro MacroOpsImpl.as_impl[Optional, S, A, CastTo]
}

class MacroTraversalOps[S, A](private val optic: Traversal[S, A]) extends AnyVal {
  def refocus[Next](lambda: A => Next): Traversal[S, Next] = macro MacroOpsImpl.refocus_impl[Traversal, S, A, Next]
  def as[CastTo <: A]: Traversal[S, CastTo] = macro MacroOpsImpl.as_impl[Traversal, S, A, CastTo]
}

class MacroSetterOps[S, A](private val optic: Setter[S, A]) extends AnyVal {
  def refocus[Next](lambda: A => Next): Setter[S, Next] = macro MacroOpsImpl.refocus_impl[Setter, S, A, Next]
  def as[CastTo <: A]: Setter[S, CastTo] = macro MacroOpsImpl.as_impl[Setter, S, A, CastTo]
}

class MacroGetterOps[S, A](private val optic: Getter[S, A]) extends AnyVal {
  def refocus[Next](lambda: A => Next): Getter[S, Next] = macro MacroOpsImpl.refocus_impl[Getter, S, A, Next]
}

class MacroFoldOps[S, A](private val optic: Fold[S, A]) extends AnyVal {
  def refocus[Next](lambda: A => Next): Fold[S, Next] = macro MacroOpsImpl.refocus_impl[Fold, S, A, Next]
  def as[CastTo <: A]: Fold[S, CastTo] = macro MacroOpsImpl.as_impl[Fold, S, A, CastTo]
}

class MacroAppliedLensOps[S, A](private val optic: AppliedLens[S, A]) extends AnyVal {
  def refocus[Next](lambda: A => Next): AppliedLens[S, Next] = macro MacroOpsImpl.refocus_impl[AppliedLens, S, A, Next]
}

class MacroAppliedPrismOps[S, A](private val optic: AppliedPrism[S, A]) extends AnyVal {
  def as[CastTo <: A]: AppliedPrism[S, CastTo] = macro MacroOpsImpl.as_impl[AppliedPrism, S, A, CastTo]
}

class MacroAppliedOptionalOps[S, A](private val optic: AppliedOptional[S, A]) extends AnyVal {
  def refocus[Next](lambda: A => Next): AppliedOptional[S, Next] =
    macro MacroOpsImpl.refocus_impl[AppliedOptional, S, A, Next]
  def as[CastTo <: A]: AppliedOptional[S, CastTo] = macro MacroOpsImpl.as_impl[AppliedOptional, S, A, CastTo]
}

class MacroAppliedTraversalOps[S, A](private val optic: AppliedTraversal[S, A]) extends AnyVal {
  def refocus[Next](lambda: A => Next): AppliedTraversal[S, Next] =
    macro MacroOpsImpl.refocus_impl[AppliedTraversal, S, A, Next]
  def as[CastTo <: A]: AppliedTraversal[S, CastTo] = macro MacroOpsImpl.as_impl[AppliedTraversal, S, A, CastTo]
}

class MacroAppliedSetterOps[S, A](private val optic: AppliedSetter[S, A]) extends AnyVal {
  def refocus[Next](lambda: A => Next): AppliedSetter[S, Next] = macro MacroOpsImpl.refocus_impl[AppliedSetter, S, A, Next]
  def as[CastTo <: A]: AppliedSetter[S, CastTo] = macro MacroOpsImpl.as_impl[AppliedSetter, S, A, CastTo]
}

class MacroAppliedGetterOps[S, A](private val optic: AppliedGetter[S, A]) extends AnyVal {
  def refocus[Next](lambda: A => Next): AppliedGetter[S, Next] = macro MacroOpsImpl.refocus_impl[AppliedGetter, S, A, Next]
}

class MacroAppliedFoldOps[S, A](private val optic: AppliedFold[S, A]) extends AnyVal {
  def refocus[Next](lambda: A => Next): AppliedFold[S, Next] = macro MacroOpsImpl.refocus_impl[AppliedFold, S, A, Next]
  def as[CastTo <: A]: AppliedFold[S, CastTo] = macro MacroOpsImpl.as_impl[AppliedFold, S, A, CastTo]
}

class MacroOpsImpl(val c: blackbox.Context) {
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

  def refocus_impl[Optic[_, _], From, To: c.WeakTypeTag, Next](lambda: c.Expr[To => Next]): c.Expr[Optic[From, Next]] = {
    import c.universe._

    val subj = c.prefix.tree match {
      case Apply(TypeApply(_, _), List(x)) => x
      case t                               => c.abort(c.enclosingPosition, s"Invalid prefix tree ${show(t)}")
    }

    c.Expr[Optic[From, Next]](
      q"""$subj.andThen(_root_.monocle.macros.GenLens[${c.weakTypeOf[To]}](${lambda}))"""
    )
  }
}
