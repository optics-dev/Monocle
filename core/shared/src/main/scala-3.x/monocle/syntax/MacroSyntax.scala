package monocle.syntax

import monocle.{Focus, Fold, Iso, Optional, Prism, Setter, Traversal}

import scala.quoted.{Expr, Quotes, Type}

trait MacroSyntax {
  implicit def toMacroPrismOps[From, To](optic: Prism[From, To]): MacroPrismOps[From, To] = new MacroPrismOps(optic)
  implicit def toMacroOptionalOps[From, To](optic: Optional[From, To]): MacroOptionalOps[From, To] = new MacroOptionalOps(optic)
  implicit def toMacroTraversalOps[From, To](optic: Traversal[From, To]): MacroTraversalOps[From, To] = new MacroTraversalOps(optic)
  implicit def toMacroSetterOps[From, To](optic: Setter[From, To]): MacroSetterOps[From, To] = new MacroSetterOps(optic)
  implicit def toMacroFoldOps[From, To](optic: Fold[From, To]): MacroFoldOps[From, To] = new MacroFoldOps(optic)
}

class MacroPrismOps[From, To](private val optic: Prism[From, To]) extends AnyVal {
  inline def as[CastTo <: To]: Prism[From, CastTo] =
    optic.andThen(GenPrism[To, CastTo])
}

class MacroOptionalOps[From, To](private val optic: Optional[From, To]) extends AnyVal {
  inline def as[CastTo <: To]: Optional[From, CastTo] =
    optic.andThen(GenPrism[To, CastTo])
}

class MacroTraversalOps[From, To](private val optic: Traversal[From, To]) extends AnyVal {
  inline def as[CastTo <: To]: Traversal[From, CastTo] =
    optic.andThen(GenPrism[To, CastTo])
}

class MacroSetterOps[From, To](private val optic: Setter[From, To]) extends AnyVal {
  inline def as[CastTo <: To]: Setter[From, CastTo] =
    optic.andThen(GenPrism[To, CastTo])
}

class MacroFoldOps[From, To](private val optic: Fold[From, To]) extends AnyVal {
  inline def as[CastTo <: To]: Fold[From, CastTo] =
    optic.andThen(GenPrism[To, CastTo])
}

private[monocle] object GenPrism {
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