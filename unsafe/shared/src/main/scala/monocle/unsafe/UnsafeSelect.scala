package monocle.unsafe

import monocle.Prism


object UnsafeSelect {
  def unsafeSelect[A](predicate: A => Boolean): Prism[A, A] =
    Prism[A, A](a => if (predicate(a)) Some(a) else None)(a => a)
}
