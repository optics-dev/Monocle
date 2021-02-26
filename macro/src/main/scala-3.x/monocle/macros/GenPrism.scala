package monocle.macros

import monocle.Prism
import scala.quoted.{Type, Expr, Quotes}

object GenPrism {
  inline def apply[From, To <: From]: Prism[From, To] =
    ${ GenPrismImpl.apply }
}

private[monocle] object GenPrismImpl {
  def apply[From: Type, To: Type](using Quotes): Expr[Prism[From, To]] =
    '{
      Prism[From, To]((from: From) => if (from.isInstanceOf[To]) Some(from.asInstanceOf[To]) else None)(
        (to: To) => to.asInstanceOf[From])
    }

}