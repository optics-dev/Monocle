package monocle.macros.syntax

import scala.reflect.macros.blackbox
import monocle.syntax.AppliedLens

object fields2 extends GenAppliedLensSyntax

trait GenAppliedLensSyntax {
  implicit def toGenAppliedLensOps[A](value: A): GenAppliedLensOps[A] = new GenAppliedLensOps(value)
}

class GenAppliedLensOps[A](private val value: A) extends AnyVal {
  def lens[C]( field: A => C ): AppliedLens[A,C] = macro GenAppliedLensOpsImpl2.lens_impl[A, C]
}

class GenAppliedLensOpsImpl2(val c: blackbox.Context){
  def lens_impl[A: c.WeakTypeTag, C](field: c.Expr[A => C]): c.Expr[AppliedLens[A,C]] = {
    import c.universe._

    val subj = c.prefix.tree match {
      case Apply(TypeApply(_, _), List(x)) => x
      case t => c.abort(c.enclosingPosition, s"Invalid prefix tree ${show(t)}")
    }

    c.Expr[AppliedLens[A,C]](q"""
      _root_.monocle.syntax.AppliedLens(
        $subj,
        _root_.monocle.macros.GenLens[${c.weakTypeOf[A]}](${field})
      )
    """)
  }
}