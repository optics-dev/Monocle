package monocle.internal

/** From cats: Represents two values of the same type that are expected to be equal.
  */
final case class IsEq[A](lhs: A, rhs: A)

object IsEq {
  implicit def syntax[A](lhs: A): IsEqOps[A] = new IsEqOps(lhs)

  final class IsEqOps[A](private val lhs: A) extends AnyVal {
    def <==>(rhs: A): IsEq[A] = IsEq(lhs, rhs)
  }
}
