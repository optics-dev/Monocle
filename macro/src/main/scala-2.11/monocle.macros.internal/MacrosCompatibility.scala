package monocle.macros.internal

import scala.reflect.internal.TreeGen

trait MacrosCompatibility {
  type Context = scala.reflect.macros.blackbox.Context

  def getDeclarations(c: Context)(tpe: c.universe.Type): c.universe.MemberScope =
    tpe.decls

  def getParameterLists(c: Context)(method: c.universe.MethodSymbol): List[List[c.universe.Symbol]] =
    method.paramLists

  def getDeclaration(c: Context)(tpe: c.universe.Type, name: c.universe.Name): c.universe.Symbol =
    tpe.decl(name)

  def createTermName(c: Context)(name: String): c.universe.TermName =
    c.universe.TermName(name)

  def createTypeName(c: Context)(name: String): c.universe.TypeName =
    c.universe.TypeName(name)

  def resetLocalAttrs(c: Context)(tree: c.Tree): c.Tree =
    c.untypecheck(tree)

  def getTermNames(c: Context): c.universe.TermNamesApi =
    c.universe.termNames

  def companionTpe(c: Context)(tpe: c.universe.Type): c.universe.Symbol =
    tpe.typeSymbol.companion

  def makeAttributedQualifier(c: Context)(tree: TreeGen, tpe: c.universe.Type): c.universe.Tree =
    tree.mkAttributedQualifier(tpe.asInstanceOf[tree.global.Type]).asInstanceOf[c.universe.Tree]
}
