package monocle.macros.syntax

import monocle.Lens

import scala.reflect.macros.blackbox

trait LensMacroSyntax {
  implicit def toLensMacroOps[S, A](value: Lens[S, A]): LensMacroOps[S, A] =
    new LensMacroOps(value)
}

class LensMacroOps[S, A](private val value: Lens[S, A]) extends AnyVal {
  def lens[C](field: A => C): Lens[S, C] =
    macro GenLensLensOpsImpl.lens_impl[S, A, C]
}

class GenLensLensOpsImpl(val c: blackbox.Context) {
  def lens_impl[S, A: c.WeakTypeTag, C](field: c.Expr[A => C]): c.Expr[Lens[S, C]] = {
    import c.universe._

    val subj = c.prefix.tree match {
      case Apply(TypeApply(_, _), List(x)) => x
      case t =>
        c.abort(c.enclosingPosition, s"Invalid prefix tree ${show(t)}")
    }

    c.Expr[Lens[S, C]](
      q"$subj.composeLens(_root_.monocle.macros.GenLens[${c.weakTypeOf[A]}](${field}))"
    )
  }
}
