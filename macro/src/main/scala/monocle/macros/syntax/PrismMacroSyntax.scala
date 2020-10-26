package monocle.macros.syntax

import monocle.{Optional, Prism}

import scala.reflect.macros.blackbox

trait PrismMacroSyntax {
  implicit def toPrismMacroOps[S, A](value: Prism[S, A]): PrismMacroOps[S, A] =
    new PrismMacroOps(value)
}

class PrismMacroOps[S, A](private val value: Prism[S, A]) extends AnyVal {
  def lens[C](field: A => C): Optional[S, C] =
    macro GenLensPrismOpsImpl.lens_impl[S, A, C]
}

class GenLensPrismOpsImpl(val c: blackbox.Context) {
  def lens_impl[S, A: c.WeakTypeTag, C](field: c.Expr[A => C]): c.Expr[Optional[S, C]] = {
    import c.universe._

    val subj = c.prefix.tree match {
      case Apply(TypeApply(_, _), List(x)) => x
      case t =>
        c.abort(c.enclosingPosition, s"Invalid prefix tree ${show(t)}")
    }

    c.Expr[Optional[S, C]](
      q"$subj.composeLens(_root_.monocle.macros.GenLens[${c.weakTypeOf[A]}](${field}))"
    )
  }
}
