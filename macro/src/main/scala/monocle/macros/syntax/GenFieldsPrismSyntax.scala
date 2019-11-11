package monocle.macros.syntax
import scala.reflect.macros.blackbox
import monocle.syntax.{AppliedPrism, AppliedOptional}

trait GenFieldsPrismSyntax {
  implicit class GenFieldsPrism[A, B](private val value: AppliedPrism[A, B]) {
    def field[C]( f: B => C ): AppliedOptional[A, C] = macro GenApplyPrismOpsImpl.field_impl[A, B, C]
  }
}

class GenApplyPrismOpsImpl(val c: blackbox.Context){
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
