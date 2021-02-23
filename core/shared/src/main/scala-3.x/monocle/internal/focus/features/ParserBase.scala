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
    def unapply(term: Term): Option[(Name, FromType, TypeArgs, ValueArgs, RemainingCode)] = term match {
      case Apply(TypeApply(Select(_, keyword), typeArgs), List(code)) => 
        Some(Name(keyword), FromType(code.tpe.widen), TypeArgs(typeArgs.map(_.tpe): _*), ValueArgs(), RemainingCode(code))

      case Apply(Apply(TypeApply(Select(_, keyword), typeArgs), List(code)), valueArgs) => 
        Some(Name(keyword), FromType(code.tpe.widen), TypeArgs(typeArgs.map(_.tpe): _*), ValueArgs(valueArgs: _*), RemainingCode(code))

      case _ => None
    }
  }

  object FocusKeywordGiven {
    def unapply(term: Term): Option[(Name, FromType, TypeArgs, ValueArgs, GivenInstance, RemainingCode)] = term match {
      case Apply(FocusKeyword(keyword, fromType, typeArgs, valueArgs, code), List(instance)) => 
        Some(keyword, fromType, typeArgs, valueArgs, GivenInstance(instance), code)

      case _ => None
    }
  }
}