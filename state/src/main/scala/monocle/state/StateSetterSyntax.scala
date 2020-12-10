package monocle.state

import cats.{Eval, Now}
import monocle.PSetter
import cats.data.IndexedStateT

trait StateSetterSyntax {
  @deprecated("no replacement", since = "3.0.0-M1")
  implicit def toStateSetterOps[S, T, A, B](setter: PSetter[S, T, A, B]): StateSetterOps[S, T, A, B] =
    new StateSetterOps[S, T, A, B](setter)
}

@deprecated("no replacement", since = "3.0.0-M1")
final class StateSetterOps[S, T, A, B](private val setter: PSetter[S, T, A, B]) extends AnyVal {

  /** modify the value referenced through the setter */
  @deprecated("no replacement", since = "3.0.0-M1")
  def mod_(f: A => B): IndexedStateT[Eval, S, T, Unit] =
    IndexedStateT(s => Now((setter.modify(f)(s), ())))

  /** set the value referenced through the setter */
  @deprecated("no replacement", since = "3.0.0-M1")
  def assign_(b: B): IndexedStateT[Eval, S, T, Unit] =
    mod_(_ => b)
}
