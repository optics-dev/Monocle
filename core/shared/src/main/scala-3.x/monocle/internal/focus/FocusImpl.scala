package monocle.internal.focus

import monocle.Lens
import scala.quoted.{Type, Expr, Quotes, quotes}

private[focus] class FocusImpl(val macroContext: Quotes) 
    extends FocusBase 
    with ErrorHandling
    with ParserLoop with AllParsers 
    with GeneratorLoop with AllGenerators {

  import macroContext.reflect._

  def run[From: Type, To: Type](lambda: Expr[From => To]): Expr[Any] = {
    val parseResult: FocusResult[List[FocusAction]] = 
      parseLambda[From](lambda.asTerm)

    val generatedCode: FocusResult[Term] = 
      parseResult.flatMap(generateCode[From])
      
    generatedCode match {
      case Right(code) => code.asExpr
      case Left(error) => report.error(errorMessage(error)); '{???}
    }
  }
}

object FocusImpl {
  def apply[From: Type, To: Type](lambda: Expr[From => To])(using Quotes): Expr[Any] =
    new FocusImpl(quotes).run(lambda)
}