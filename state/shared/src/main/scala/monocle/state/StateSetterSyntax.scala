package monocle.state

import cats.{Eval, Now}
import monocle.PSetter
import cats.data.IndexedStateT

trait StateSetterSyntax {
  implicit def toStateSetterOps[S, T, A, B](setter: PSetter[S, T, A, B]): StateSetterOps[S, T, A, B] =
    new StateSetterOps[S, T, A, B](setter)
}

final class StateSetterOps[S, T, A, B](setter: PSetter[S, T, A, B]) {
  /** modify the value referenced through the setter */
  def mod_(f: A => B): IndexedStateT[Eval, S, T, Unit] =
    IndexedStateT(s => Now((setter.modify(f)(s), ())))

  /** set the value referenced through the setter */
  def assign_(b: B): IndexedStateT[Eval, S, T, Unit] =
    mod_(_ => b)
}
