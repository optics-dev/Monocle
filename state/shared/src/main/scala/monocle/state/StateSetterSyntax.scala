package monocle.state

import monocle.PSetter

import scalaz.IndexedState

trait StateSetterSyntax {
  implicit def toStateSetterOps[S, T, A, B](setter: PSetter[S, T, A, B]): StateSetterOps[S, T, A, B] =
    new StateSetterOps[S, T, A, B](setter)
}

final class StateSetterOps[S, T, A, B](private val setter: PSetter[S, T, A, B]) extends AnyVal {
  /** modify the value referenced through the setter */
  def mod_(f: A => B): IndexedState[S, T, Unit] =
    IndexedState(s => (setter.modify(f)(s), ()))

  /** set the value referenced through the setter */
  def assign_(b: B): IndexedState[S, T, Unit] =
    mod_(_ => b)
}
