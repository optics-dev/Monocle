package monocle.internal.focus.features

import scala.quoted.Quotes
import monocle.internal.focus.FocusBase

private[focus] trait ParserBase {
  this: FocusBase => 

  import macroContext.reflect._

  trait FocusParser {
    def unapply(term: Term): Option[FocusResult[(RemainingCode, FocusAction)]]
  }

  case class Name(name: String)
  case class GivenInstance(instance: Term)
  case class RemainingCode(code: Term)
  case class FromType(fromType: TypeRepr)
  case class TypeArgs(args: TypeRepr*)
  case class ValueArgs(args: Term*)

  object FocusKeyword {
    def unapply(term: Term): Option[(Name, FromType, TypeArgs, RemainingCode)] = term match {
      case Apply(TypeApply(Select(_, keyword), typeArgs), List(code)) => 
        Some(Name(keyword), FromType(code.tpe.widen), TypeArgs(typeArgs.map(_.tpe): _*), RemainingCode(code))

      case _ => None
    }
  }

  object FocusKeywordGiven {
    def unapply(term: Term): Option[(Name, FromType, TypeArgs, ValueArgs, GivenInstance, RemainingCode)] = term match {
      case Apply(Apply(FocusKeyword(keyword, fromType, typeArgs, code), argList), List(instance)) => 
        Some(keyword, fromType, typeArgs, ValueArgs(argList: _*), GivenInstance(instance), code)

      case Apply(FocusKeyword(keyword, fromType, typeArgs, code), List(instance)) => 
        Some(keyword, fromType, typeArgs, ValueArgs(), GivenInstance(instance), code)

      case _ => None
    }
  }
}