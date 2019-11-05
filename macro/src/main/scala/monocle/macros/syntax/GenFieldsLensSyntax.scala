package monocle.macros.syntax
import scala.reflect.macros.blackbox
import monocle.syntax.AppliedLens

trait GenFieldsLensSyntax {
  implicit class GenFieldsLens[A, B](private val value: AppliedLens[A, B]) {
    def field[C]( f: B => C ): AppliedLens[A, C] = macro GenApplyLensOpsImpl.lens_impl[A, B, C]
  }
}

class GenApplyLensOpsImpl(val c: blackbox.Context){
  def lens_impl[A, B: c.WeakTypeTag, C](f: c.Expr[B => C]): c.Expr[AppliedLens[A,C]] = {
    import c.universe._

    val subj = c.prefix.tree match {
      case Apply(TypeApply(_, _), List(x)) => x
      case t =>
        c.abort(c.enclosingPosition, s"Invalid prefix tree ${show(t)}")
    }

    c.Expr[AppliedLens[A,C]](q"""
      $subj.compose(_root_.monocle.macros.GenLens[${c.weakTypeOf[B]}](${f}))
    """)
  }
}
