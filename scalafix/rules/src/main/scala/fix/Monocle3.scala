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
    private[this] val setOptionMatcher =
      SymbolMatcher.normalized("monocle/POptional#setOption.")

    def apply(t: Tree)(implicit doc: SemanticDocument): List[Patch] =
      t.collect {
        case setMatcher(t: Term.Name) =>
          Patch.renameSymbol(t.symbol, "replace")
        case setOptionMatcher(t: Term.Name) =>
          Patch.renameSymbol(t.symbol, "replaceOption")

      }
  }
}
