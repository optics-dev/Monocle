package monocle.internal.focus.features

import scala.quoted.Quotes
import monocle.internal.focus.FocusBase

private[focus] trait KeywordParserBase extends ParserBase {
  this: FocusBase => 

  import macroContext.reflect._

  // Marker classes for type safety
  case class Name(name: String)
  case class GivenInstance(instance: Term)
  case class FromType(fromType: TypeRepr)
  case class TypeArgs(args: TypeRepr*)
  case class ValueArgs(args: Term*)

  object FocusKeyword {
    def unapply(term: Term): Option[(Name, FromType, TypeArgs, ValueArgs, RemainingCode)] = term match {
      // No value args, inferred type arguments `[T] keyword`
      case Apply(TypeApply(Select(_, keyword), typeArgs), List(code)) => 
        Some(Name(keyword), FromType(code.tpe.widen), TypeArgs(typeArgs.map(_.tpe): _*), ValueArgs(), RemainingCode(code))

      // Value args required, inferred type arguments `[T] keyword(value)`
      case Apply(Apply(TypeApply(Select(_, keyword), typeArgs), List(code)), valueArgs) => 
        Some(Name(keyword), FromType(code.tpe.widen), TypeArgs(typeArgs.map(_.tpe): _*), ValueArgs(valueArgs: _*), RemainingCode(code))

      // No value args, direct type arguments `keyword[T]`
      case TypeApply(Apply(Select(_, keyword), List(code)), typeArgs) => 
        Some(Name(keyword), FromType(code.tpe.widen), TypeArgs(typeArgs.map(_.tpe): _*), ValueArgs(), RemainingCode(code))

      case _ => None
    }
  }

  object FocusKeywordGiven {
    def unapply(term: Term): Option[(Name, FromType, TypeArgs, ValueArgs, GivenInstance, RemainingCode)] = term match {
      // Value args required, inferred type arguments, inferred instance `[T](using instance) keyword(value)`
      case Apply(FocusKeyword(keyword, fromType, typeArgs, valueArgs, code), List(instance)) => 
        Some(keyword, fromType, typeArgs, valueArgs, GivenInstance(instance), code)

      case _ => None
    }
  }
}