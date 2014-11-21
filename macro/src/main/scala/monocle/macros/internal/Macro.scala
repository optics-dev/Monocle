package monocle.macros.internal

import monocle.Lens

import scala.language.experimental.macros

object Macro {
  def mkLens[A, B](fieldName: String): Lens[A, B] = macro MacroImpl.mkLens_impl[A, B]
}

private[macros] object MacroImpl {

  import scala.reflect.macros._

  def lenser_impl[A: c.WeakTypeTag, B: c.WeakTypeTag](c: Context)(field: c.Expr[A => B]): c.Expr[Lens[A, B]] = {
    import c.universe._
    val fieldName = field match {
      case Expr(
      Function(
      List(ValDef(_, termDefName, _, EmptyTree)),
      Select(Ident(termUseName), fieldNameName))) if termDefName.decodedName.toString == termUseName.decodedName.toString =>
        fieldNameName.decodedName.toString
      case _ => c.abort(c.enclosingPosition, s"Illegal field reference ${show(field.tree)}; please use _.field instead")
    }

    mkLens_impl[A, B](c)(c.Expr[String](q"$fieldName"))
  }

  def mkLens_impl[A: c.WeakTypeTag, B: c.WeakTypeTag](c: Context)(fieldName: c.Expr[String]): c.Expr[Lens[A, B]] = {
    import c.universe._

    val (aTpe, bTpe) = (weakTypeOf[A], weakTypeOf[B])

    val getter = mkGetter_impl[A, B](c)(fieldName)
    val setter = mkSetter_impl[A, B](c)(fieldName)

    c.Expr[Lens[A, B]](q"""
      import monocle.Lens
      Lens[$aTpe, $bTpe]($getter)($setter)
    """)
  }

  def mkGetter_impl[A: c.WeakTypeTag, B: c.WeakTypeTag](c: Context)(fieldName: c.Expr[String]): c.Expr[B] = {
    import c.universe._
    val aTpe = weakTypeOf[A]

    val strFieldName = c.eval(c.Expr[String](c.resetLocalAttrs(fieldName.tree.duplicate)))

    val fieldMethod = aTpe.declarations.collectFirst {
      case m: MethodSymbol if m.isCaseAccessor && m.name.decodedName.toString == strFieldName => m
    }.getOrElse(c.abort(c.enclosingPosition, s"Cannot find method $strFieldName in $aTpe"))

    c.Expr[B](q"""{(a: $aTpe) => a.$fieldMethod}""")
  }

  def mkSetter_impl[A: c.WeakTypeTag, B: c.WeakTypeTag](c: Context)(fieldName: c.Expr[String]): c.Expr[B => A => A] = {
    import c.universe._
    val (aTpe, bTpe) = (weakTypeOf[A], weakTypeOf[B])

    val constructor = aTpe.declarations.collectFirst {
      case m: MethodSymbol if m.isPrimaryConstructor => m
    }.getOrElse(c.abort(c.enclosingPosition, s"Cannot find constructor in $aTpe"))

    val strFieldName = c.eval(c.Expr[String](c.resetLocalAttrs(fieldName.tree.duplicate)))

    val field = constructor.paramss.head.find(_.name.decodedName.toString == strFieldName).getOrElse(c.abort(c.enclosingPosition, s"Cannot find constructor field named $fieldName in $aTpe"))

    c.Expr[B => A => A](q"{b: $bTpe => a: $aTpe => a.copy(${field} = b)}")
  }

}
