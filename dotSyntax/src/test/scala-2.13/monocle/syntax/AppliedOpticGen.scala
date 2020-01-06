package monocle.syntax

case class AppliedOpticGen[A](value: A) {
  val iso: AppliedIso[A, A]           = AppliedIso.id(value)
  val prism: AppliedPrism[A, A]       = iso
  val lens: AppliedLens[A, A]         = iso
  val optional: AppliedOptional[A, A] = iso
  val setter: AppliedSetter[A, A]     = iso
  val getter: AppliedGetter[A, A]     = iso
  val fold: AppliedFold[A, A]         = iso
}
