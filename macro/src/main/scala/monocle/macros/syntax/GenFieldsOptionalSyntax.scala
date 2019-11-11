package monocle.macros.syntax
import scala.reflect.macros.blackbox
import monocle.syntax.AppliedOptional

trait GenFieldsOptionalSyntax {
  implicit class GenFieldsOptional[A, B](private val value: AppliedOptional[A, B]) {
    def field[C]( f: B => C ): AppliedOptional[A, C] = macro GenApplyOptionalOpsImpl.field_impl[A, B, C]
  }
}

class GenApplyOptionalOpsImpl(val c: blackbox.Context){
  def field_impl[A, B: c.WeakTypeTag, C](f: c.Expr[B => C]): c.Expr[AppliedOptional[A,C]] = {
    import c.universe._

    val subj = c.prefix.tree match {
      case Apply(TypeApply(_, _), List(x)) => x
      case t =>
        c.abort(c.enclosingPosition, s"Invalid prefix tree ${show(t)}")
    }

    c.Expr[AppliedOptional[A,C]](q"""
      $subj.compose(_root_.monocle.macros.GenLens[${c.weakTypeOf[B]}](${f}))
    """)
  }
}
