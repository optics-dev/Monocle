package monocle.macros.internal

trait MacrosCompatibility {
  type Context = scala.reflect.macros.Context
  type WhiteboxContext = scala.reflect.macros.Context

  def getDeclarations(c: Context)(tpe: c.universe.Type): c.universe.MemberScope =
    tpe.declarations

  def getParameterLists(c: Context)(method: c.universe.MethodSymbol): List[List[c.universe.Symbol]] =
    method.paramss

  def getDeclaration(c: Context)(tpe: c.universe.Type, name: c.universe.Name): c.universe.Symbol =
    tpe.declaration(name)

  def createTermName(c: Context)(name: String): c.universe.TermName =
    c.universe.newTermName(name)

  def createTypeName(c: Context)(name: String): c.universe.TypeName =
    c.universe.newTypeName(name)

  def resetLocalAttrs(c: Context)(tree: c.Tree): c.Tree =
    c.resetLocalAttrs(tree)

  def getTermNames(c: Context): c.universe.TermNamesApi =
    c.universe.`nme`

  def companionTpe(c: Context)(tpe: c.universe.Type): c.universe.Symbol =
    tpe.typeSymbol.companionSymbol
}