package monocle.macros

import scala.reflect.macros.blackbox

class Lenses(prefix: String = "") extends scala.annotation.StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro LensesImpl.lensesAnnotationMacro
}

class PLenses(prefix: String = "") extends scala.annotation.StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro LensesImpl.plensesAnnotationMacro
}

private[macros] class LensesImpl(val c: blackbox.Context) {
  def lensesAnnotationMacro(annottees: c.Expr[Any]*): c.Expr[Any] = annotationMacro(annottees, poly = false)

  def plensesAnnotationMacro(annottees: c.Expr[Any]*): c.Expr[Any] = annotationMacro(annottees, poly = true)

  def annotationMacro(annottees: Seq[c.Expr[Any]], poly: Boolean): c.Expr[Any] = {
    import c.universe._

    val LensesTpe = TypeName(if (poly) "PLenses" else "Lenses")
    val prefix = c.macroApplication match {
      case Apply(Select(Apply(Select(New(Ident(LensesTpe)), t), args), _), _) if t == termNames.CONSTRUCTOR =>
        args match {
          case Literal(Constant(s: String)) :: Nil => s
          case _                                   => ""
        }
      case _ => ""
    }

    def monolenses(tpname: TypeName, params: List[ValDef]): List[Tree] =
      params.map { param =>
        val lensName = TermName(prefix + param.name.decodedName)
        q"""val $lensName =
        monocle.macros.internal.Macro.mkLens[$tpname, $tpname, ${param.tpt}, ${param.tpt}](${param.name.toString})"""
      }

    def lenses(tpname: TypeName, tparams: List[TypeDef], params: List[ValDef]): List[Tree] =
      if (tparams.isEmpty)
        monolenses(tpname, params)
      else
        params.map { param =>
          val lensName = TermName(prefix + param.name.decodedName)
          val q"x: $s" = q"x: $tpname[..${tparams.map(_.name)}]"
          val q"x: $a" = q"x: ${param.tpt}"
          q"""def $lensName[..$tparams] =
            monocle.macros.internal.Macro.mkLens[$s, $s, $a, $a](${param.name.toString})"""
        }

    def plenses(tpname: TypeName, tparams: List[TypeDef], params: List[ValDef]): List[Tree] =
      if (tparams.isEmpty)
        monolenses(tpname, params)
      else {
        // number of fields in which each tparam is used
        val tparamsUsages: Map[TypeName, Int] = params.foldLeft(tparams.map(_.name -> 0).toMap) { (acc, param) =>
          val typeNames = param.collect { case Ident(tn: TypeName) => tn }.toSet
          typeNames.foldLeft(acc)((map, key) => map.get(key).fold(map)(value => map.updated(key, value + 1)))
        }

        val groupedTpnames: Map[Int, Set[TypeName]] =
          tparamsUsages.toList.groupBy(_._2).map { case (n, tps) => (n, tps.map(_._1).toSet) }
        val phantomTpnames     = groupedTpnames.getOrElse(0, Set.empty)
        val singleFieldTpnames = groupedTpnames.getOrElse(1, Set.empty)

        params.map { param =>
          val lensName        = TermName(prefix + param.name.decodedName)
          val tpnames         = param.collect { case Ident(tn: TypeName) => tn }.toSet
          val tpnamesToChange = tpnames.intersect(singleFieldTpnames) ++ phantomTpnames
          val tpnamesMap = tpnamesToChange.foldLeft((tparams.map(_.name).toSet ++ tpnames).map(x => (x, x)).toMap) {
            (acc, tpname) => acc.updated(tpname, c.freshName(tpname))
          }
          val defParams = tparams ++ tparams
            .filter(x => tpnamesToChange.contains(x.name))
            .map { case TypeDef(mods, name, tps, rhs) =>
              TypeDef(mods, tpnamesMap(name), tps, rhs)
            }
            .toSet

          object tptTransformer extends Transformer {
            override def transform(tree: Tree): Tree =
              tree match {
                case Ident(tn: TypeName) => Ident(tpnamesMap(tn))
                case x                   => super.transform(x)
              }
          }

          val q"x: $s" = q"x: $tpname[..${tparams.map(_.name)}]"
          val q"x: $t" = q"x: $tpname[..${tparams.map(x => tpnamesMap(x.name))}]"
          val q"x: $a" = q"x: ${param.tpt}"
          val q"x: $b" = q"x: ${tptTransformer.transform(param.tpt)}"

          q"""def $lensName[..$defParams] =
               monocle.macros.internal.Macro.mkLens[$s, $t, $a, $b](${param.name.toString})"""
        }
      }

    val lensDefs = if (poly) plenses _ else lenses _

    val result = annottees map (_.tree) match {
      case (classDef @ q"$mods class $tpname[..$tparams] $ctorMods(...$paramss) extends { ..$earlydefns } with ..$parents { $self => ..$stats }")
          :: Nil if mods.hasFlag(Flag.CASE) =>
        val name = tpname.toTermName
        q"""
         $classDef
         object $name {
           ..${lensDefs(tpname, tparams, paramss.head)}
         }
         """
      case (classDef @ q"$mods class $tpname[..$tparams] $ctorMods(...$paramss) extends { ..$earlydefns } with ..$parents { $self => ..$stats }")
          :: q"$objMods object $objName extends { ..$objEarlyDefs } with ..$objParents { $objSelf => ..$objDefs }"
          :: Nil if mods.hasFlag(Flag.CASE) =>
        q"""
         $classDef
         $objMods object $objName extends { ..$objEarlyDefs} with ..$objParents { $objSelf =>
           ..${lensDefs(tpname, tparams, paramss.head)}
           ..$objDefs
         }
         """
      case _ => c.abort(c.enclosingPosition, "Invalid annotation target: must be a case class")
    }

    c.Expr[Any](result)
  }
}
