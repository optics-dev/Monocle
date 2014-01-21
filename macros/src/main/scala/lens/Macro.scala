package lens

import scala.language.experimental.macros
import scala.reflect.macros.Context
import lens.impl.HLens

object Macro {

  def generateLens[A, B](field: String): HLens[A, B] = macro generateLens_impl[A, B]

  def generateLens_impl[A: c.WeakTypeTag, B: c.WeakTypeTag](c : Context)(field: c.Expr[String]): c.Expr[HLens[A, B]] = {
    import c.universe._

    val (aTpe, bTpe) =  (weakTypeOf[A], weakTypeOf[B])

    val fieldMethod =  aTpe.declarations.collectFirst {
      case m : MethodSymbol if m.isCaseAccessor => m.name
    }.getOrElse(c.abort(c.enclosingPosition, s"Cannot find method $field in ${weakTypeOf[A]}"))

    val quasi = q"""n
      new HLens[$aTpe, $bTpe] {
        protected def lensFunction[F[_] : Functor](lift: $bTpe => F[$bTpe], from: $aTpe): F[$aTpe] =
          Functor[F].map(lift(from.$fieldMethod))(newValue => from.copy($fieldMethod = newValue))
      }"""

    c.Expr[HLens[A, B]](quasi)
  }

  def get[A, B](from: A, field: String): B = macro get_impl[A, B]

  def get_impl[A: c.WeakTypeTag, B: c.WeakTypeTag](c : Context)(from: c.Expr[A],  field: c.Expr[String]): c.Expr[B] = {
    import c.universe._
    val (aTpe, bTpe) =  (weakTypeOf[A], weakTypeOf[B])

    val fieldMethod =  aTpe.declarations.collectFirst {
      case m : MethodSymbol if m.isCaseAccessor => m.name
    }.getOrElse(c.abort(c.enclosingPosition, s"Cannot find method $field in ${weakTypeOf[A]}"))


    val quasi = q"""$from.$fieldMethod"""
    c.Expr[B](quasi)
  }

  def mkSetter[A, B](fieldName: String): (A,B) => A = macro mkSetter_impl[A,B]

  def mkSetter_impl[A: c.WeakTypeTag, B: c.WeakTypeTag](c : Context)(fieldName: c.Expr[String]): c.Expr[(A,B) => A] = {
    import c.universe._
    val (aTpe, bTpe) =  (weakTypeOf[A], weakTypeOf[B])

    val constructor = aTpe.declarations.collectFirst {
      case m: MethodSymbol if m.isPrimaryConstructor => m
    }.getOrElse(c.abort(c.enclosingPosition, s"Cannot find constructor in ${weakTypeOf[A]}"))

    val field = constructor.paramss.head.find(
      _.name.decoded == fieldName.toString()
    ).getOrElse(c.abort(c.enclosingPosition, s"Cannot find constructor field named in $fieldName"))

    c.Expr[(A,B) => A](q"{(a: $aTpe, b: $bTpe) => a.copy(${field.name} = b)}")
  }

}
