package monocle.macros.syntax

import monocle.Lens
import monocle.syntax.AppliedLens

import scala.reflect.macros.blackbox

object fields extends FieldsSyntax

trait FieldsSyntax {
  implicit class FieldAppliedLensOps[A, B](value: AppliedLens[A, B]) {
    def field[C](field: B => C): AppliedLens[A, C] = macro GenAppliedLensOpsImpl.lens_impl[A, B, C]
    def field2[C](field: B => C): Lens[B, C] = macro GenAppliedLensOpsImpl.lens_impl2[B, C]
  }
}


class GenAppliedLensOpsImpl(val c: blackbox.Context){

  def lens_impl2[ B: c.WeakTypeTag, C](field: c.Expr[B => C]): c.Expr[Lens[B, C]] = {
    import c.universe._
    val fieldLens = c.Expr[Lens[B, C]](q"_root_.monocle.macros.GenLens[${c.weakTypeOf[B]}](${field})")
    fieldLens
  }

  def lens_impl[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag](field: c.Expr[B => C]): c.Expr[AppliedLens[A, C]] = {
    import c.universe._

    val subj = c.prefix.tree match {
      case Apply(TypeApply(_, _), List(x)) => x
      case t => c.abort(c.enclosingPosition, s"Invalid prefix tree ${show(t)}")
    }

    val appliedOptic = c.Expr[AppliedLens[A, B]](subj)

    println("foo")

    val fieldLens    = c.Expr[Lens[B, C]](q"monocle.macros.GenLens[${c.weakTypeOf[B]}](${field})")

    println("bar")

    val code: c.universe.Tree = q"""$appliedOptic.composeLens($fieldLens)"""

    println(showCode(code))
    println(showCode(subj))
    println(show(subj))
    println(showRaw(subj))

    c.Expr[AppliedLens[A, C]](code)
  }
}