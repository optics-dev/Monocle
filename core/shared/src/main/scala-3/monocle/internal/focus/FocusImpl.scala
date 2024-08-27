package monocle.internal.focus

import monocle.Focus
import scala.quoted.{quotes, Expr, Quotes, Type}
import monocle.internal.focus.features.{AllFeatureGenerators, AllFeatureParsers, GeneratorLoop, ParserLoop}

private[focus] class FocusImpl(val macroContext: Quotes)
    extends FocusBase
    with ErrorHandling
    with LambdaConfigParser
    with ParserLoop
    with AllFeatureParsers
    with GeneratorLoop
    with AllFeatureGenerators {

  import macroContext.reflect.*

  def run[From: Type, To: Type](lambda: Expr[Focus.KeywordContext ?=> From => To]): Expr[Any] = {
    val generatedCode =
      for {
        config       <- parseLambdaConfig[From](lambda.asTerm)
        focusActions <- parseFocusActions(config)
        code         <- generateCode[From](focusActions)
      } yield code

    generatedCode match {
      case Right(code) => code.asExpr
      case Left(error) =>
        val (msg, pos) = errorReport(error)
        report.errorAndAbort(msg, pos.getOrElse(lambda.asTerm.pos))
    }
  }
}

private[monocle] object FocusImpl {
  def apply[From: Type, To: Type](lambda: Expr[Focus.KeywordContext ?=> From => To])(using Quotes): Expr[Any] =
    new FocusImpl(quotes).run(lambda)
}
