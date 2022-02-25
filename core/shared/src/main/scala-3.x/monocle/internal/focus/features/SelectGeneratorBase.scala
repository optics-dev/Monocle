package monocle.internal.focus.features

import monocle.internal.focus.FocusBase
import scala.annotation.tailrec

private[focus] trait SelectGeneratorBase {
  this: FocusBase =>

  import this.macroContext.reflect._

  def generateGetter(from: Term, fieldName: String): Term =
    Select.unique(from, fieldName) // o.field

  @tailrec
  final def etaExpandIfNecessary(term: Term): Term =
    if (term.isExpr) {
      term
    } else {
      val expanded: Term = term.etaExpand(Symbol.spliceOwner)

      val implicits: List[Term] = expanded match {
        case Block(List(DefDef(_, List(params), _, _)), _) =>
          params.params.map {
            case ValDef(_, t, _) =>
              val typeRepr: TypeRepr = t.tpe.dealias
              Implicits.search(typeRepr) match {
                case success: ImplicitSearchSuccess => success.tree
                case _ =>
                  report.errorAndAbort(
                    s"Couldn't find assumed implicit for ${typeRepr.show}. Neither " +
                      s"multiple (non-implicit) parameter sets nor default arguments for implicits are supported."
                  )
              }
            case other =>
              report.errorAndAbort(
                s"Expected a value definition as parameter but found $other."
              )
          }
        case other =>
          report.errorAndAbort(
            s"Expected code block with eta expanded function but found $other."
          )
      }

      etaExpandIfNecessary(Apply(term, implicits))
    }
}
