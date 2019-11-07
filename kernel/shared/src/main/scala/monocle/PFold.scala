package monocle

trait PFold[-S, +T, +A, -B] extends Serializable {
  def fold[Z](zero: Z, combine: (Z, Z) => Z)(f: A => Z)(s: S): Z
}