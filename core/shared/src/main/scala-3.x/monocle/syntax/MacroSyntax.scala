package monocle.syntax

import monocle._

import scala.quoted.{Expr, Quotes, Type}

trait MacroSyntax {

  extension [From, To] (optic: Prism[From, To]) {
    inline def as[CastTo <: To]: Prism[From, CastTo] =
      optic.andThen(AsPrism[To, CastTo])
  }

  extension [From, To] (optic: Optional[From, To]) {
    inline def as[CastTo <: To]: Optional[From, CastTo] =
      optic.andThen(AsPrism[To, CastTo])
  }

  extension [From, To] (optic: Traversal[From, To]) {
    inline def as[CastTo <: To]: Traversal[From, CastTo] =
      optic.andThen(AsPrism[To, CastTo])
  }

  extension [From, To] (optic: Setter[From, To]) {
    inline def as[CastTo <: To]: Setter[From, CastTo] =
      optic.andThen(AsPrism[To, CastTo])
  }

  extension [From, To] (optic: Fold[From, To]) {
    inline def as[CastTo <: To]: Fold[From, CastTo] =
      optic.andThen(AsPrism[To, CastTo])
  }

  extension [From, To] (optic: AppliedPrism[From, To]) {
    inline def as[CastTo <: To]: AppliedPrism[From, CastTo] =
      optic.andThen(AsPrism[To, CastTo])
  }

  extension [From, To] (optic: AppliedOptional[From, To]) {
    inline def as[CastTo <: To]: AppliedOptional[From, CastTo] =
      optic.andThen(AsPrism[To, CastTo])
  }

  extension [From, To] (optic: AppliedTraversal[From, To]) {
    inline def as[CastTo <: To]: AppliedTraversal[From, CastTo] =
      optic.andThen(AsPrism[To, CastTo])
  }

  extension [From, To] (optic: AppliedSetter[From, To]) {
    inline def as[CastTo <: To]: AppliedSetter[From, CastTo] =
      optic.andThen(AsPrism[To, CastTo])
  }

  extension [From, To] (optic: AppliedFold[From, To]) {
    inline def as[CastTo <: To]: AppliedFold[From, CastTo] =
      optic.andThen(AsPrism[To, CastTo])
  }

}

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