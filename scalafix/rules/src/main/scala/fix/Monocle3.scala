package fix

import scalafix.v1._
import scala.meta._

class Monocle3 extends SemanticRule("Monocle") {

  override def fix(implicit doc: SemanticDocument): Patch = {
    SetRules(doc.tree)
  }.asPatch

  object SetRules {
    private[this] val setMatcher =
      SymbolMatcher.normalized("monocle/PLens#set.")
    private[this] val composeLensMatcher =
      SymbolMatcher.normalized("monocle/PLens#composeLens.")
    private[this] val setOptionMatcher =
      SymbolMatcher.normalized("monocle/POptional#setOption.")
    private[this] val atMatcher =
      SymbolMatcher.normalized("monocle/function/AtFunctions#at.")

    def apply(t: Tree)(implicit doc: SemanticDocument): List[Patch] =
      t.collect {
        case setMatcher(t: Term.Name) =>
          Patch.renameSymbol(t.symbol, "replace")
        case setOptionMatcher(t: Term.Name) =>
          Patch.renameSymbol(t.symbol, "replaceOption")
        case composeLensMatcher(
              t @ Term.ApplyInfix(
                lhs: Term,
                op: Name,
                targs: List[Type],
                Term.Apply((Term.Name("at"), b)) :: Nil
              )
            ) =>
          val r = Term.ApplyInfix(
            lhs,
            Term.Name("at"),
            targs,
            b
          )
          println(r)
          Patch.replaceTree(t, r.toString())

      }
  }
}
