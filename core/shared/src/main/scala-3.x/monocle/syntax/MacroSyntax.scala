package monocle.syntax

import monocle._
import monocle.internal.{AsPrism, IsoFields}
import scala.deriving.Mirror

trait MacroSyntax {

  extension (isoCompanion: Iso.type) {
    
    /** Generate an [[Iso]] between a case class `S` and its fields.
      *
      * Case classes with 0 fields will correspond with `EmptyTuple`, 1 with `Tuple1[field type]`, 2 or more with
      * a tuple of all field types in the same order as the fields themselves.
      */
    transparent inline def fields[S <: Product : Mirror.ProductOf]: Iso[S, Tuple] =
      IsoFields[S]
  }

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