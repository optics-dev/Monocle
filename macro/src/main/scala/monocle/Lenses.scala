package monocle

import scala.language.experimental.macros

class Lenses extends scala.annotation.StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro LensesImpl.annotationMacro
}

private[monocle] object LensesImpl {

  import scala.reflect.macros._

  def annotationMacro(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    val result = annottees map (_.tree) match {
      case (classDef @ q"$mods class $tpname[..$tparams] $ctorMods(...$paramss) extends { ..$earlydefns } with ..$parents { $self => ..$stats }")
        :: Nil if mods.hasFlag(Flag.CASE) =>
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
        :: Nil if mods.hasFlag(Flag.CASE) =>
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
      case _ => c.abort(c.enclosingPosition, "Invalid annotation target: must be a case class")
    }

    c.Expr[Any](result)
  }


}
