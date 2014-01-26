package lens

import scala.language.experimental.macros
import scala.reflect.macros.Context

object Macro {

  def mkLens[A, B](fieldName: String): Lens[A, B] = macro MacroImpl.mkLens_impl[A, B]

}

private[lens] object MacroImpl {

  def mkLens_impl[A: c.WeakTypeTag, B: c.WeakTypeTag](c : Context)(fieldName: c.Expr[String]): c.Expr[Lens[A, B]] = {
    import c.universe._

    val (aTpe, bTpe) =  (weakTypeOf[A], weakTypeOf[B])

    val getter = mkGetter_impl[A,B](c)(fieldName)
    val setter = mkSetter_impl[A,B](c)(fieldName)

    c.Expr[Lens[A, B]](q"""
      import lens.impl.HLens
      HLens[$aTpe, $bTpe]($getter, $setter)
    """)
  }

  def mkGetter_impl[A: c.WeakTypeTag, B: c.WeakTypeTag](c : Context)(fieldName: c.Expr[String]): c.Expr[B] = {
    import c.universe._
    val aTpe =  weakTypeOf[A]

    val strFieldName = c.eval(c.Expr[String](c.resetAllAttrs(fieldName.tree.duplicate)))

    val fieldMethod =  aTpe.declarations.collectFirst {
      case m : MethodSymbol if m.isCaseAccessor && m.name.decoded == strFieldName => m.name
    }.getOrElse(c.abort(c.enclosingPosition, s"Cannot find method $strFieldName in $aTpe"))


    c.Expr[B](q"""{(a: $aTpe) => a.$fieldMethod}""")
  }

  def mkSetter_impl[A: c.WeakTypeTag, B: c.WeakTypeTag](c : Context)(fieldName: c.Expr[String]): c.Expr[(A,B) => A] = {
    import c.universe._
    val (aTpe, bTpe) =  (weakTypeOf[A], weakTypeOf[B])

    val constructor = aTpe.declarations.collectFirst {
      case m: MethodSymbol if m.isPrimaryConstructor => m
    }.getOrElse(c.abort(c.enclosingPosition, s"Cannot find constructor in $aTpe"))

    val strFieldName = c.eval(c.Expr[String](c.resetAllAttrs(fieldName.tree.duplicate)))

    val field = constructor.paramss.head.find(_.name.decoded == strFieldName).getOrElse(c.abort(c.enclosingPosition, s"Cannot find constructor field named $fieldName in $aTpe"))

    c.Expr[(A,B) => A](q"{(a: $aTpe, b: $bTpe) => a.copy(${field.name} = b)}")
  }

}
