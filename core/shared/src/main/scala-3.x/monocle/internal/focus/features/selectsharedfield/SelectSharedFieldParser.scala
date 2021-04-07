package monocle.internal.focus.features.selectsharedfield

import monocle.internal.focus.FocusBase
import monocle.internal.focus.features.SelectParserBase

private[focus] trait SelectSharedFieldParser {
  this: FocusBase with SelectParserBase => 

  import this.macroContext.reflect._
  
  object SelectSharedField extends FocusParser {

    def unapply(term: Term): Option[FocusResult[(RemainingCode, FocusAction)]] = term match {
      
      case Select(remainingCode, fieldName) if isEnumWithChildrenThatAllHaveCaseField(remainingCode, fieldName) => 
        val fromType = getType(remainingCode)
        val action = getFieldAction(fromType, fieldName, getEnumChildren(fromType, remainingCode))
        val remainingCodeWithAction = action.map(a => (RemainingCode(remainingCode), a))
        Some(remainingCodeWithAction)

      case _ => None
    }
  }

  private def getEnumChildren(fromType: TypeRepr, code: Term): List[TypeRepr] = {
    fromType.classSymbol match {
      case Some(classSym) => classSym.children.flatMap(getTypeReprFromEnumChildSymbol).toList
      case None => Nil
    }
  }

  object MyTraverser extends TreeTraverser {
    override def traverseTree(tree: Tree)(owner: Symbol): Unit = {
      println(tree)
      traverseTreeChildren(tree)(owner)
    }
  }

  private def getTypeReprFromEnumChildSymbol(childSym: Symbol): Option[TypeRepr] = {
    // childSym.tree match {
    // case ClassDef(name, DefDef(_, _, typeTree, _), parents, _, _) => Some(typeTree.tpe)
    // }
    MyTraverser.traverseTree(childSym.tree)(childSym)
    None
  }

  private def swapInSuppliedTypeArgs(enumChild: TypeRepr, supplied: List[TypeRepr]): TypeRepr = {
    ???
  }

  private def isEnumWithChildrenThatAllHaveCaseField(code: Term, fieldName: String): Boolean = {

    def allEnumChildrenHaveField(sym: Symbol): Boolean = 
      sym.children.forall(isCaseClassWithField)

    def isCaseClassWithField(sym: Symbol): Boolean = 
      sym.flags.is(Flags.Case) && sym.caseFields.exists(_.name == fieldName)

    def isSealed(sym: Symbol): Boolean = 
      sym.flags.is(Flags.Sealed)

    code.tpe.classSymbol.exists(sym => isSealed(sym) && allEnumChildrenHaveField(sym))
  }

  private def getFieldAction(fromType: TypeRepr, fieldName: String, enumChildren: List[TypeRepr]): FocusResult[FocusAction] = {
    getFieldType(fromType, fieldName).flatMap { toType => 
      Right(FocusAction.SelectSharedField(fieldName, fromType, getSuppliedTypeArgs(fromType), toType, enumChildren))
    }
  }
}