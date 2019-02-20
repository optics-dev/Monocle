package monocle.macros.syntax
import scala.reflect.macros.blackbox
import monocle.syntax.ApplyLens

trait GenApplyLensSyntax {
  implicit def toGenApplyLensOps[S](value: S): GenApplyLensOps[S] = new GenApplyLensOps(value)
}

class GenApplyLensOps[A](private val value: A) extends AnyVal {
  def lens[C]( field: A => C ): ApplyLens[A,A,C,C] = macro GenApplyLensOpsImpl.lens_impl[A, C]
}

@macrocompat.bundle
class GenApplyLensOpsImpl(val c: blackbox.Context){
  def lens_impl[A: c.WeakTypeTag, C](field: c.Expr[A => C]): c.Expr[ApplyLens[A,A,C,C]] = {
    import c.universe._

    val subj = c.prefix.tree match {
      case Apply(TypeApply(_, _), List(x)) => x
      case t =>
        c.abort(c.enclosingPosition, s"Invalid prefix tree ${show(t)}")
    }

    c.Expr[ApplyLens[A,A,C,C]](q"""
      _root_.monocle.syntax.ApplyLens(
        $subj,
        _root_.monocle.macros.GenLens[${c.weakTypeOf[A]}](${field})
      )
    """)
  }
}
