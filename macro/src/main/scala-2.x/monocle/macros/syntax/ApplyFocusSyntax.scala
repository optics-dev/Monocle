package monocle.macros.syntax

import monocle.syntax.ApplyPIso
import monocle.{ApplyIso, ApplyLens, Iso}

import scala.reflect.macros.blackbox

trait ApplyFocusSyntax {
  implicit def toApplyFocusOps[S](value: S): ApplyFocusOps[S] = new ApplyFocusOps(value)
}

class ApplyFocusOps[A](private val value: A) extends AnyVal {
  @deprecated("use focus", since = "3.0.0-M1")
  def lens[C](field: A => C): ApplyLens[A, C] = macro GenApplyLensOpsImpl.lens_impl[A, C]
  def focus[C](field: A => C): ApplyLens[A, C] = macro GenApplyLensOpsImpl.lens_impl[A, C]

  def focus(): ApplyIso[A, A] = ApplyPIso(value, Iso.id)
}

class GenApplyLensOpsImpl(val c: blackbox.Context) {
  def lens_impl[A: c.WeakTypeTag, C](field: c.Expr[A => C]): c.Expr[ApplyLens[A, C]] = {
    import c.universe._

    val subj = c.prefix.tree match {
      case Apply(TypeApply(_, _), List(x)) => x
      case t =>
        c.abort(c.enclosingPosition, s"Invalid prefix tree ${show(t)}")
    }

    c.Expr[ApplyLens[A, C]](q"""
      _root_.monocle.syntax.ApplyPLens(
        $subj,
        _root_.monocle.macros.GenLens[${c.weakTypeOf[A]}](${field})
      )
    """)
  }
}
