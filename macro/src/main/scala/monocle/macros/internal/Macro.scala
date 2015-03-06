package monocle.macros.internal

import monocle.Lens

object Macro {
  def mkLens[S, A](fieldName: String): Lens[S, A] = macro MacroImpl.mkLens_impl[S, A]
}

private[macros] object MacroImpl extends MacrosCompatibility {

  def genLens_impl[S: c.WeakTypeTag, A: c.WeakTypeTag](c: Context)(field: c.Expr[S => A]): c.Expr[Lens[S, A]] = {
    import c.universe._
    val fieldName = field match {
      case Expr(
      Function(
      List(ValDef(_, termDefName, _, EmptyTree)),
      Select(Ident(termUseName), fieldNameName))) if termDefName.decodedName.toString == termUseName.decodedName.toString =>
        fieldNameName.decodedName.toString
      case _ => c.abort(c.enclosingPosition, s"Illegal field reference ${show(field.tree)}; please use _.field instead")
    }

    mkLens_impl[S, A](c)(c.Expr[String](q"$fieldName"))
  }

  def mkLens_impl[S: c.WeakTypeTag, A: c.WeakTypeTag](c: Context)(fieldName: c.Expr[String]): c.Expr[Lens[S, A]] = {
    import c.universe._

    val (sTpe, aTpe) = (weakTypeOf[S], weakTypeOf[A])

    val strFieldName = c.eval(c.Expr[String](resetLocalAttrs(c)(fieldName.tree.duplicate)))

    val fieldMethod = getDeclarations(c)(sTpe).collectFirst {
      case m: MethodSymbol if m.isCaseAccessor && m.name.decodedName.toString == strFieldName => m
    }.getOrElse(c.abort(c.enclosingPosition, s"Cannot find method $strFieldName in $sTpe"))

    val constructor = getDeclarations(c)(sTpe).collectFirst {
      case m: MethodSymbol if m.isPrimaryConstructor => m
    }.getOrElse(c.abort(c.enclosingPosition, s"Cannot find constructor in $sTpe"))

    val field = getParameterLists(c)(constructor).head
      .find(_.name.decodedName.toString == strFieldName)
      .getOrElse(c.abort(c.enclosingPosition, s"Cannot find constructor field named $fieldName in $sTpe"))

    c.Expr[Lens[S, A]](q"""
      monocle.PLens[$sTpe, $sTpe, $aTpe, $aTpe](_.$fieldMethod)(a => _.copy($field = a))
    """)
  }

}
