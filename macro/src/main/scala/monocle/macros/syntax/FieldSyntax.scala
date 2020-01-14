package monocle.macros.syntax

import scala.reflect.macros.blackbox
import monocle.{Fold, Getter, Lens, Optional, Prism, Setter}
import monocle.syntax.{AppliedFold, AppliedGetter, AppliedLens, AppliedOptional, AppliedPrism, AppliedSetter}

trait FieldSyntax_Priority3 {
  implicit class GenFieldsFold[A, B](private val value: Fold[A, B]) {
    def field[C](f: B => C): Fold[A, C] = macro FieldSyntaxImpl.field_impl[Fold, A, B, C]
  }

  implicit class GenFieldsAppliedFold[A, B](private val value: AppliedFold[A, B]) {
    def field[C](f: B => C): AppliedFold[A, C] = macro FieldSyntaxImpl.field_impl[AppliedFold, A, B, C]
  }
}

trait FieldSyntax_Priority2 extends FieldSyntax_Priority3 {
  implicit class GenFieldsOptional[A, B](private val value: Optional[A, B]) {
    def field[C](f: B => C): Optional[A, C] = macro FieldSyntaxImpl.field_impl[Optional, A, B, C]
  }

  implicit class GenFieldsAppliedOptional[A, B](private val value: AppliedOptional[A, B]) {
    def field[C](f: B => C): AppliedOptional[A, C] = macro FieldSyntaxImpl.field_impl[AppliedOptional, A, B, C]
  }

  implicit class GenFieldsGetter[A, B](private val value: Getter[A, B]) {
    def field[C](f: B => C): Getter[A, C] = macro FieldSyntaxImpl.field_impl[Getter, A, B, C]
  }

  implicit class GenFieldsSetter[A, B](private val value: Setter[A, B]) {
    def field[C](f: B => C): Setter[A, C] = macro FieldSyntaxImpl.field_impl[Setter, A, B, C]
  }

  implicit class GenFieldsAppliedGetter[A, B](private val value: AppliedGetter[A, B]) {
    def field[C](f: B => C): AppliedGetter[A, C] = macro FieldSyntaxImpl.field_impl[AppliedGetter, A, B, C]
  }

  implicit class GenFieldsAppliedSetter[A, B](private val value: AppliedSetter[A, B]) {
    def field[C](f: B => C): AppliedSetter[A, C] = macro FieldSyntaxImpl.field_impl[AppliedSetter, A, B, C]
  }
}

trait FieldSyntax_Priority1 extends FieldSyntax_Priority2 {
  implicit class GenFieldsPrism[A, B](private val value: Prism[A, B]) {
    def field[C](f: B => C): Optional[A, C] = macro FieldSyntaxImpl.field_impl[Optional, A, B, C]
  }

  implicit class GenFieldsAppliedPrism[A, B](private val value: AppliedPrism[A, B]) {
    def field[C](f: B => C): AppliedOptional[A, C] = macro FieldSyntaxImpl.field_impl[AppliedOptional, A, B, C]
  }
}

trait FieldSyntax extends FieldSyntax_Priority1 {
  implicit class GenFieldsLens[A, B](private val value: Lens[A, B]) {
    def field[C](f: B => C): Lens[A, C] = macro FieldSyntaxImpl.field_impl[Lens, A, B, C]
  }

  implicit class GenFieldsAppliedLens[A, B](private val value: AppliedLens[A, B]) {
    def field[C](f: B => C): AppliedLens[A, C] = macro FieldSyntaxImpl.field_impl[AppliedLens, A, B, C]
  }
}

class FieldSyntaxImpl(val c: blackbox.Context) {
  def field_impl[R[_, _], A, B: c.WeakTypeTag, C](f: c.Expr[B => C]): c.Expr[R[A, C]] = {
    import c.universe._

    val subj = c.prefix.tree match {
      case Apply(TypeApply(_, _), List(x)) => x
      case t =>
        c.abort(c.enclosingPosition, s"Invalid prefix tree ${show(t)}")
    }

    c.Expr[R[A, C]](q"""
      $subj.andThen(_root_.monocle.macros.GenLens[${c.weakTypeOf[B]}](${f}))
    """)
  }
}
