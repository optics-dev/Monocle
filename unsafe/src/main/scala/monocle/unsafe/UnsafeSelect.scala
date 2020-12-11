package monocle.unsafe

import monocle.Prism

object UnsafeSelect {
  @deprecated("use optic.filter(predicate)", since = "3.0.0-M1")
  def unsafeSelect[A](predicate: A => Boolean): Prism[A, A] =
    Prism[A, A](a => Some(a).filter(predicate))(a => a)
}
