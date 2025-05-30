package monocle.macros.syntax

import monocle.syntax.AppliedPIso
import monocle.{AppliedIso, AppliedLens, Iso}

import scala.reflect.macros.blackbox

trait AppliedFocusSyntax {
  implicit def toAppliedFocusOps[S](value: S): AppliedFocusOps[S] = new AppliedFocusOps(value)
}

class AppliedFocusOps[A](private val value: A) extends AnyVal {
  @deprecated("use focus", since = "3.0.0-M1")
  def lens[C](lambda: A => C): AppliedLens[A, C] = macro GenAppliedLensOpsImpl.lens_impl[A, C]
  def focus[C](lambda: A => C): AppliedLens[A, C] = macro GenAppliedLensOpsImpl.lens_impl[A, C]

  def focus(): AppliedIso[A, A] = AppliedPIso(value, Iso.id)
}

class GenAppliedLensOpsImpl(val c: blackbox.Context) {
  def lens_impl[A: c.WeakTypeTag, C](lambda: c.Expr[A => C]): c.Expr[AppliedLens[A, C]] = {
    import c.universe._

    val subj = c.prefix.tree match {
      case Apply(TypeApply(_, _), List(x)) => x
      case t                               =>
        c.abort(c.enclosingPosition, s"Invalid prefix tree ${show(t)}")
    }

    c.Expr[AppliedLens[A, C]](q"""
      _root_.monocle.syntax.AppliedPLens(
        $subj,
        _root_.monocle.macros.GenLens[${c.weakTypeOf[A]}]($lambda)
      )
    """)
  }
}
