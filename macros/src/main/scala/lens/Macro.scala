package lens

import scala.language.experimental.macros
import scala.reflect.macros.Context

object Macro {

  def mkLens[A, B](fieldName: String): Lens[A, B] = macro mkLens_impl[A, B]

  def mkLens_impl[A: c.WeakTypeTag, B: c.WeakTypeTag](c : Context)(fieldName: c.Expr[String]): c.Expr[Lens[A, B]] = {
    import c.universe._

    val (aTpe, bTpe) =  (weakTypeOf[A], weakTypeOf[B])

    val getter = mkGetter_impl[A,B](c)(fieldName)
    val setter = mkSetter_impl[A,B](c)(fieldName)

    val quasi2 = q"""
      import lens.impl.HLens
      HLens[$aTpe, $bTpe]($getter, $setter)
    """

    c.Expr[Lens[A, B]](quasi2)
  }

  def mkGetter[A, B](field: String): A => B = macro mkGetter_impl[A, B]

  def mkGetter_impl[A: c.WeakTypeTag, B: c.WeakTypeTag](c : Context)(field: c.Expr[String]): c.Expr[B] = {
    import c.universe._
    val aTpe =  weakTypeOf[A]

    val fieldMethod =  aTpe.declarations.collectFirst {
      case m : MethodSymbol if m.isCaseAccessor => m.name
    }.getOrElse(c.abort(c.enclosingPosition, s"Cannot find method $field in ${weakTypeOf[A]}"))


    val quasi = q"""{(a: $aTpe) => a.$fieldMethod}"""
    c.Expr[B](quasi)
  }

  def mkSetter[A, B](fieldName: String): (A,B) => A = macro mkSetter_impl[A,B]

  def mkSetter_impl[A: c.WeakTypeTag, B: c.WeakTypeTag](c : Context)(fieldName: c.Expr[String]): c.Expr[(A,B) => A] = {
    import c.universe._
    val (aTpe, bTpe) =  (weakTypeOf[A], weakTypeOf[B])

    val constructor = aTpe.declarations.collectFirst {
      case m: MethodSymbol if m.isPrimaryConstructor => m
    }.getOrElse(c.abort(c.enclosingPosition, s"Cannot find constructor in $aTpe"))

    val field = constructor.paramss.head.headOption.getOrElse(c.abort(c.enclosingPosition, s"Cannot find constructor field named $fieldName in $aTpe"))

    c.Expr[(A,B) => A](q"{(a: $aTpe, b: $bTpe) => a.copy(${field.name} = b)}")
  }

}
