package monocle.syntax

import monocle.{Fold, Getter, Iso, Lens, Optional, Prism, Setter}

case class OpticGen[A]() {
  val iso: Iso[A, A]           = Iso.id
  val prism: Prism[A, A]       = iso
  val lens: Lens[A, A]         = iso
  val optional: Optional[A, A] = iso
  val setter: Setter[A, A]     = iso
  val getter: Getter[A, A]     = iso
  val fold: Fold[A, A]         = iso
}
