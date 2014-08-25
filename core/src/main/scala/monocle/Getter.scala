package monocle

trait Getter[S, A] { self =>

  def get(from: S): A

  final def asGetter: Getter[S, A] = self

  /** non overloaded compose function */
  final def composeGetter[B](other: Getter[A, B]): Getter[S, B] = new Getter[S, B] {
    def get(from: S): B = other.get(self.get(from))
  }

  @deprecated("Use composeGetter", since = "0.5")
  def compose[B](other: Getter[A, B]): Getter[S, B] = composeGetter(other)

}

object Getter {
  def apply[S, A](_get: S => A): Getter[S, A] = new Getter[S, A] {
    def get(from: S): A = _get(from)
  }
}
