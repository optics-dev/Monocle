package monocle.internal.focus.features.fieldselect

import monocle.internal.focus.FocusBase

private[focus] trait FieldSelectParser {
  this: FocusBase => 

  import this.macroContext.reflect._
  
  object FieldSelect extends FocusParser {

    def unapply(term: Term): Option[FocusResult[(Term, FocusAction)]] = term match {
      case Select(CaseClass(remainingCode), fieldName) => 
        val action = getFieldAction(getFromType(remainingCode), fieldName)
        val remainingCodeWithAction = action.map(a => (remainingCode, a))
        Some(remainingCodeWithAction)

      case Select(remainingCode, fieldName) => 
        Some(FocusError.NotACaseClass(remainingCode.tpe.show, fieldName).asResult)
        
      case _ => None
    }

  }

  private def getFieldAction(fromType: TypeRepr, fieldName: String): FocusResult[FocusAction] = {
    getFieldType(fromType, fieldName) match {
      case Some(toType) => Right(FocusAction.FieldSelect(fieldName, fromType, getSuppliedTypeArgs(fromType), toType))
      case None => FocusError.CouldntFindFieldType(fromType.show, fieldName).asResult
    }
  }

  private def getFromType(remainingCode: Term): TypeRepr = 
    remainingCode.tpe.widen

  private def getFieldType(fromType: TypeRepr, fieldName: String): Option[TypeRepr] = {
    fromType.classSymbol.flatMap { 
      _.memberField(fieldName) match {
        case FieldType(possiblyTypeArg) => Some(swapWithSuppliedType(fromType, possiblyTypeArg))
        case _ => None
      }
    }
  }

  private object FieldType {
    def unapply(fieldSymbol: Symbol): Option[TypeRepr] = fieldSymbol match {
      case sym if sym.isNoSymbol => None
      case sym => sym.tree match {
        case ValDef(_, typeTree, _) => Some(typeTree.tpe)
        case _ => None
      }
    }
  }

  private def getSuppliedTypeArgs(fromType: TypeRepr): List[TypeRepr] = {
    fromType match {
      case AppliedType(_, argTypeReprs) => argTypeReprs 
      case _ => Nil
    }
  }

  object CaseClass {
    def unapply(term: Term): Option[Term] =
      term.tpe.classSymbol.flatMap { sym => 
        Option.when(sym.flags.is(Flags.Case))(term)
      }
  }


  private def getDeclaredTypeArgs(classType: TypeRepr): List[Symbol] = {
    classType.classSymbol.map(_.primaryConstructor.paramSymss) match {
      case Some(typeParamList :: _) if typeParamList.exists(_.isTypeParam) => typeParamList
      case _ => Nil
    }
  }

  private def swapWithSuppliedType(fromType: TypeRepr, possiblyContainsTypeArgs: TypeRepr): TypeRepr = {
    val declared = getDeclaredTypeArgs(fromType)
    val supplied = getSuppliedTypeArgs(fromType)
    val swapDict = declared.view.map(_.name).zip(supplied).toMap
    
    def swapInto(candidate: TypeRepr): TypeRepr = {
      candidate match {
        case AppliedType(typeCons, args) => swapInto(typeCons).appliedTo(args.map(swapInto))
        case leafType => swapDict.getOrElse(leafType.typeSymbol.name, leafType)
      }
    }
    swapInto(possiblyContainsTypeArgs)
  }
}