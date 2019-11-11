package monocle.macros.internal

import monocle.Lens

import scala.reflect.macros.blackbox

object Macro {
  def mkLens[A, B](fieldName: String): Lens[A, B] = macro MacroImpl.mkLens_impl[A, B]
}

private[macros] class MacroImpl(val c: blackbox.Context) {
  def genLens_impl[A: c.WeakTypeTag, B: c.WeakTypeTag](field: c.Expr[A => B]): c.Expr[Lens[A, B]] = {
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
        mkLens_impl[A, B](c.Expr[String](q"$fieldName"))

      // _.field1.field2...
      case Expr(
        Function(
          List(ValDef(_, termDefName, _, EmptyTree)),
          SelectChain(termUseName, typesFields)
        )
      ) if termDefName.decodedName.toString == termUseName.decodedName.toString =>
        c.Expr[Lens[A, B]](
          typesFields.map{ case (t,f) => q"_root_.monocle.macros.GenLens[$t](_.$f)" }
                     .reduce((a,b) => q"$a compose $b")
        )

      case _ => c.abort(c.enclosingPosition, s"Illegal field reference ${show(field.tree)}; please use _.field1.field2... instead")
    }
  }

  def mkLens_impl[A: c.WeakTypeTag,  B: c.WeakTypeTag](fieldName: c.Expr[String]): c.Expr[Lens[A, B]] = {
    import c.universe._

    val (aTpe, bTpe) = (weakTypeOf[A], weakTypeOf[B])

    val strFieldName = c.eval(c.Expr[String](c.untypecheck(fieldName.tree.duplicate)))

    val fieldMethod = aTpe.decls.collectFirst {
      case m: MethodSymbol if m.isCaseAccessor && m.name.decodedName.toString == strFieldName => m
    }.getOrElse(c.abort(c.enclosingPosition, s"Cannot find method $strFieldName in $aTpe"))

    val constructor = aTpe.decls.collectFirst {
      case m: MethodSymbol if m.isPrimaryConstructor => m
    }.getOrElse(c.abort(c.enclosingPosition, s"Cannot find constructor in $aTpe"))

    val field = constructor.paramLists.head
      .find(_.name.decodedName.toString == strFieldName)
      .getOrElse(c.abort(c.enclosingPosition, s"Cannot find constructor field named $fieldName in $aTpe"))

    val F = TypeName(c.freshName("F"))

    c.Expr[Lens[A, B]](q"""
      import monocle.Lens

      new Lens[$aTpe, $bTpe]{
        override def get(s: $aTpe): $bTpe =
          s.$fieldMethod

        override def set(a: $bTpe): $aTpe => $aTpe =
          _.copy($field = a)
      }
    """)
  }

}
