package monocle

import scala.language.experimental.macros
import monocle.internal.CompatibilityMacro210._

class Lenses extends scala.annotation.StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro MacroImpl.annotationMacro
}

object Macro {

  def mkLens[A, B](fieldName: String): Lens[A, A, B, B] = macro MacroImpl.mkLens_impl[A, B]

}

private[monocle] object MacroImpl {

  import scala.reflect.macros._

  def annotationMacro(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    val result = annottees map (_.tree) match {
      case (classDef @ q"$mods class $tpname[..$tparams] $ctorMods(...$paramss) extends { ..$earlydefns } with ..$parents { $self => ..$stats }")
        :: Nil =>
        val name = tpname.toTermName
        val lenses = paramss.head map (param =>
          q"""val ${param.name} = monocle.Macro.mkLens[$tpname, ${param.tpt}](${param.name.toString})"""
          )
        q"""
         $classDef
         object $name {
           ..$lenses
         }
         """
      case (classDef @ q"$mods class $tpname[..$tparams] $ctorMods(...$paramss) extends { ..$earlydefns } with ..$parents { $self => ..$stats }")
        :: q"object $objName {..$objDefs}"
        :: Nil =>
        val lenses = paramss.head map (param =>
          q"""val ${param.name} = monocle.Macro.mkLens[$tpname, ${param.tpt}](${param.name.toString})"""
          )
        q"""
         $classDef
         object $objName {
           ..$lenses
           ..$objDefs
         }
         """
      case _ => c.abort(c.enclosingPosition, "Invalid annotation target")
    }

    c.Expr[Any](result)
  }

  def mkLens_impl[A: c.WeakTypeTag, B: c.WeakTypeTag](c: Context)(fieldName: c.Expr[String]): c.Expr[Lens[A, A, B, B]] = {
    import c.universe._

    val (aTpe, bTpe) = (weakTypeOf[A], weakTypeOf[B])

    val getter = mkGetter_impl[A, B](c)(fieldName)
    val setter = mkSetter_impl[A, B](c)(fieldName)

    c.Expr[Lens[A, A, B, B]](q"""
      import monocle.Lens
      Lens[$aTpe, $aTpe, $bTpe, $bTpe]($getter, $setter)
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

  def mkSetter_impl[A: c.WeakTypeTag, B: c.WeakTypeTag](c: Context)(fieldName: c.Expr[String]): c.Expr[(A, B) => A] = {
    import c.universe._
    val (aTpe, bTpe) = (weakTypeOf[A], weakTypeOf[B])

    val constructor = aTpe.declarations.collectFirst {
      case m: MethodSymbol if m.isPrimaryConstructor => m
    }.getOrElse(c.abort(c.enclosingPosition, s"Cannot find constructor in $aTpe"))

    val strFieldName = c.eval(c.Expr[String](c.resetLocalAttrs(fieldName.tree.duplicate)))

    val field = constructor.paramss.head.find(_.name.decodedName.toString == strFieldName).getOrElse(c.abort(c.enclosingPosition, s"Cannot find constructor field named $fieldName in $aTpe"))

    c.Expr[(A, B) => A](q"{(a: $aTpe, b: $bTpe) => a.copy(${field} = b)}")
  }

}
