package monocle.internal

import monocle.Prism

import scala.quoted.{Expr, Quotes, Type}

private[monocle] object AsPrism {
  inline def apply[From, To]: Prism[From, To] =
    ${ AsPrismImpl.apply }
}

private[monocle] object AsPrismImpl {
  def apply[From: Type, To: Type](using Quotes): Expr[Prism[From, To]] =
    '{
      Prism[From, To]((from: From) => if (from.isInstanceOf[To]) Some(from.asInstanceOf[To]) else None)(
        (to: To) => to.asInstanceOf[From])
    }
}
