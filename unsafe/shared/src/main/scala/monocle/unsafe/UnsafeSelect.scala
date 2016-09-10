package monocle.unsafe

import monocle.Prism


/**
  * Created by Cesar on 16/07/2016.
  */
object UnsafeSelect {
  def unsafeSelect[A](predicate: A => Boolean): Prism[A, A] =
    Prism[A, A](a => if (predicate(a)) Some(a) else None)(a => a)
}
