package monocle.macros

import monocle.{Iso, Lens}
import monocle.macros.internal.MacroImpl

class GenLens[A] {
  def apply(): Iso[A, A] = Iso.id

  /** generate a [[Lens]] between a case class `S` and one of its field */
  def apply[B](field: A => B): Lens[A, B] = macro MacroImpl.genLens_impl[A, B]
}

object GenLens {
  def apply[A] = new GenLens[A]
}
