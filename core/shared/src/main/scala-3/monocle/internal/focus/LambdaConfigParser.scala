package monocle.internal.focus

import scala.quoted.Type

private[focus] trait LambdaConfigParser {
  this: FocusBase =>

  import macroContext.reflect.*

  def parseLambdaConfig[From: Type](lambda: Term): FocusResult[LambdaConfig] = {
    val fromType           = TypeRepr.of[From]
    val fromTypeIsConcrete = fromType.classSymbol.isDefined

    lambda match {
      case WithKeywordContext(ExpectedLambdaFunction(config)) if fromTypeIsConcrete => Right(config)
      case WithKeywordContext(ExpectedLambdaFunction(_)) => FocusError.NotAConcreteClass(fromType.show).asResult
      case WithKeywordContext(_)                         => FocusError.NotASimpleLambdaFunction.asResult
      case _                                             => FocusError.CouldntUnderstandKeywordContext.asResult
    }
  }

  private object WithKeywordContext {
    def unapply(lambdaWithMagic: Term): Option[Term] = unwrap(lambdaWithMagic) match {
      case Block(List(DefDef(_, _, _, Some(magicFreeLambda))), _) => Some(magicFreeLambda)
      case _                                                      => None
    }
  }

  private def unwrap(term: Term): Term =
    term match {
      case Block(List(), inner) => unwrap(inner)
      case Inlined(_, _, inner) => unwrap(inner)
      case x                    => x
    }

  private object ExpectedLambdaFunction {
    def unapply(term: Term): Option[LambdaConfig] =
      unwrap(term) match {
        case Lambda(List(ValDef(argName, _, _)), body) => Some(LambdaConfig(argName, body))
        case _                                         => None
      }
  }
}
