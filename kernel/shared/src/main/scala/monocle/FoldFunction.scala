package monocle

trait FoldFunction[-S, +A] {
  def apply[Z](zero: Z, combine: (Z, Z) => Z)(f: A => Z)(s: S): Z
}