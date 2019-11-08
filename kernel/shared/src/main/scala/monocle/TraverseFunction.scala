package monocle

trait TraverseFunction[-S, +T, +A, -B] {
  def apply[Z](z: Z, f: (Z, A) => Either[Z, (Z, B)]): S => (Z, T)
}