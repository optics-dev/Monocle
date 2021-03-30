package monocle.internal.focus.features.selectmultifield

import monocle.internal.focus.FocusBase
import monocle.internal.focus.features.SelectParserBase

private[focus] trait SelectMultiFieldParser {
  this: FocusBase with SelectParserBase => 

  import this.macroContext.reflect._
  
  object SelectMultiField extends FocusParser {

    def unapply(term: Term): Option[FocusResult[(RemainingCode, FocusAction)]] = term match {
      
      case Select(remainingCode, fieldName) if isEnumWithChildrenThatAllHaveCaseField(remainingCode, fieldName) => 
        val fromType = getType(remainingCode)
        val action = getFieldAction(fromType, fieldName, getEnumChildren(fromType, remainingCode))
        val remainingCodeWithAction = action.map(a => (RemainingCode(remainingCode), a))
        Some(remainingCodeWithAction)

      case _ => None
    }
  }

  
  // object EnumWithSharedField {
  //   def unapply(term: Term): Option[(Term, String, List[TypeRepr])] = term match {
  //     case Select(code, fieldName) if isEnumWithChildrenThatAllHaveCaseField(code, fieldName) => 
  //       getEnumChildren(code).map(children => (code, fieldName, children))
  //     case _ => None
  //   }
  // }

  private def getEnumChildren(fromType: TypeRepr, code: Term): List[TypeRepr] = {
    fromType.classSymbol match {
      case Some(classSym) => classSym.children.flatMap(getTypeReprFromEnumChildSymbol).toList
      case None => Nil
    }
  }

  private def getTypeReprFromEnumChildSymbol(childSym: Symbol): Option[TypeRepr] = childSym.tree match {
    case ClassDef(name, DefDef(_, _, typeTree, _), parents, _, _, _) => {println("PARENTS"); parents foreach println; Some(typeTree.tpe)}
    case _ => None
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
      Right(FocusAction.SelectMultiField(fieldName, fromType, getSuppliedTypeArgs(fromType), toType, enumChildren))
    }
  }
}