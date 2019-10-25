package monocle.macros.internal

import monocle.Lens

import scala.reflect.macros.blackbox

object Macro {
  def mkLens[A, B](fieldName: String): Lens[A, B] = macro MacroImpl.mkLens_impl[A, B]
}

private[macros] class MacroImpl(val c: blackbox.Context) {
  def genLens_impl[S: c.WeakTypeTag, A: c.WeakTypeTag](field: c.Expr[S => A]): c.Expr[Lens[S, A]] = {
    import c.universe._

    /** Extractor for member select chains.
        e.g.: SelectChain.unapply(a.b.c) == Some("a",Seq(a.type -> "b", a.b.type -> "c")) */
    object SelectChain{
      def unapply(tree: Tree): Option[(Name,Seq[(Type,TermName)])] = tree match {
        case Select(tail@Ident(termUseName), field:TermName) =>
          Some((termUseName,Seq(tail.tpe.widen -> field)))
        case Select(tail, field:TermName) => SelectChain.unapply(tail).map(
          t => t.copy(_2 = t._2 :+ (tail.tpe.widen -> field))
        )
        case _ => None
      }
    }

    field match {
      // _.field
      case Expr(
        Function(
          List(ValDef(_, termDefName, _, EmptyTree)),
          Select(Ident(termUseName), fieldNameName)
        )
      ) if termDefName.decodedName.toString == termUseName.decodedName.toString =>
        val fieldName = fieldNameName.decodedName.toString
        mkLens_impl[S, A](c.Expr[String](q"$fieldName"))

      // _.field1.field2...
      case Expr(
        Function(
          List(ValDef(_, termDefName, _, EmptyTree)),
          SelectChain(termUseName, typesFields)
        )
      ) if termDefName.decodedName.toString == termUseName.decodedName.toString =>
        c.Expr[Lens[S, A]](
          typesFields.map{ case (t,f) => q"monocle.macros.GenLens[$t](_.$f)" }
                     .reduce((a,b) => q"$a composeLens $b")
        )

      case _ => c.abort(c.enclosingPosition, s"Illegal field reference ${show(field.tree)}; please use _.field1.field2... instead")
    }
  }

  def mkLens_impl[S: c.WeakTypeTag, A: c.WeakTypeTag](fieldName: c.Expr[String]): c.Expr[Lens[S, A]] = {
    import c.universe._

    val (sTpe, aTpe) = (weakTypeOf[S], weakTypeOf[A])

    val strFieldName = c.eval(c.Expr[String](c.untypecheck(fieldName.tree.duplicate)))

    val fieldMethod = sTpe.decls.collectFirst {
      case m: MethodSymbol if m.isCaseAccessor && m.name.decodedName.toString == strFieldName => m
    }.getOrElse(c.abort(c.enclosingPosition, s"Cannot find method $strFieldName in $sTpe"))

    val constructor = sTpe.decls.collectFirst {
      case m: MethodSymbol if m.isPrimaryConstructor => m
    }.getOrElse(c.abort(c.enclosingPosition, s"Cannot find constructor in $sTpe"))

    val field = constructor.paramLists.head
      .find(_.name.decodedName.toString == strFieldName)
      .getOrElse(c.abort(c.enclosingPosition, s"Cannot find constructor field named $fieldName in $sTpe"))

    val F = TypeName(c.freshName("F"))

    c.Expr[Lens[S, A]](q"""
      import monocle.Lens

      new Lens[$sTpe, $aTpe] {
        def get(from: $sTpe): $aTpe = s.$fieldMethod
        def set(to: $aTpe): $sTpe => $sTpe = _.copy($field = a)
      }
    """)
  }

}
