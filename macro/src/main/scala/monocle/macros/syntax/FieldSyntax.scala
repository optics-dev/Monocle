package monocle.macros.syntax
import scala.reflect.macros.blackbox
import monocle.syntax.{AppliedLens, AppliedPrism, AppliedOptional}

trait FieldSyntax_Priority2 {

  implicit class GenFieldsOptional[A, B](private val value: AppliedOptional[A, B]) {
    def field[C]( f: B => C ): AppliedOptional[A, C] = macro FieldSyntaxImpl.appliedOptional_field_impl[A, B, C]
  }

}

trait FieldSyntax_Priority1 extends FieldSyntax_Priority2 {

  implicit class GenFieldsPrism[A, B](private val value: AppliedPrism[A, B]) {
    def field[C]( f: B => C ): AppliedOptional[A, C] = macro FieldSyntaxImpl.appliedPrism_field_impl[A, B, C]
  }

}

trait FieldSyntax extends FieldSyntax_Priority1 {

  implicit class GenFieldsLens[A, B](private val value: AppliedLens[A, B]) {
    def field[C]( f: B => C ): AppliedLens[A, C] = macro FieldSyntaxImpl.appliedLens_field_impl[A, B, C]
  }

}

class FieldSyntaxImpl(val c: blackbox.Context){

  // TODO refactor to reduce duplication

  def appliedLens_field_impl[A, B: c.WeakTypeTag, C](f: c.Expr[B => C]): c.Expr[AppliedLens[A,C]] = {
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

  def appliedPrism_field_impl[A, B: c.WeakTypeTag, C](f: c.Expr[B => C]): c.Expr[AppliedOptional[A,C]] = {
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

  def appliedOptional_field_impl[A, B: c.WeakTypeTag, C](f: c.Expr[B => C]): c.Expr[AppliedOptional[A,C]] = {
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
