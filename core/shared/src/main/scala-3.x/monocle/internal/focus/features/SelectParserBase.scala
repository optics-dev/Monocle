package monocle.internal.focus.features

import monocle.internal.focus.FocusBase

private[focus] trait SelectParserBase extends ParserBase {
  this: FocusBase => 

  import this.macroContext.reflect._
  
  // Match on a term that is an instance of a case class
  object CaseClass {
    def unapply(term: Term): Option[Term] =
      term.tpe.classSymbol.flatMap { sym => 
        Option.when(sym.flags.is(Flags.Case))(term)
      }
  }

  def getSuppliedTypeArgs(fromType: TypeRepr): List[TypeRepr] = {
    fromType match {
      case AppliedType(_, argTypeReprs) => argTypeReprs 
      case _ => Nil
    }
  }

  def getClassSymbol(tpe: TypeRepr): FocusResult[Symbol] = tpe.classSymbol match {
    case Some(sym) => Right(sym)
    case None => FocusError.NotAConcreteClass(tpe.show).asResult 
  }

  def getFieldType(fromType: TypeRepr, fieldName: String): FocusResult[TypeRepr] = {
    // We need to do this to support tuples, because even though they conform as case classes in other respects, 
    // for some reason their field names (_1, _2, etc) have a space at the end, ie `_1 `.
    def getTrimmedFieldSymbol(fromTypeSymbol: Symbol): Symbol = 
      fromTypeSymbol.memberFields.find(_.name.trim == fieldName).getOrElse(Symbol.noSymbol)

    getClassSymbol(fromType).flatMap { fromTypeSymbol => 
      getTrimmedFieldSymbol(fromTypeSymbol) match {
        case FieldType(possiblyTypeArg) => Right(swapWithSuppliedType(fromType, possiblyTypeArg))
        case _ => FocusError.CouldntFindFieldType(fromType.show, fieldName).asResult
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

  private def getDeclaredTypeArgs(classType: TypeRepr): List[Symbol] = {
    classType.classSymbol.map(_.primaryConstructor.paramSymss) match {
      case Some(typeParamList :: _) if typeParamList.exists(_.isTypeParam) => typeParamList
      case _ => Nil
    }
  }
}