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

    def apply(t: Tree)(implicit doc: SemanticDocument): List[Patch] =
      t.collect {
        // case Defn.Val(_, _, tpe, rhs) if containsWithBody(rhs) =>
        //   tpe.map(removeExternalF).asPatch
        // case Defn.Def(_, _, _, _, tpe, rhs) if containsWithBody(rhs) =>
        //   tpe.map(removeExternalF).asPatch
        // case Defn.Var(_, _, tpe, rhs) if rhs.exists(containsWithBody) =>
        //   tpe.map(removeExternalF).asPatch
        // case Term.Apply(
        //       Term.Select(_, fm @ Term.Name("flatMap")),
        //       List(Term.Apply(Term.Select(_, withBodyMatcher(_)), _))
        //     ) =>
        //   Patch.replaceTree(fm, "map")
        case setMatcher(t: Term.Name) =>
          Patch.renameSymbol(t.symbol, "replace")

      }
  }
}
