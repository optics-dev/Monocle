package monocle

trait Setter[A, B] { self =>
  def set(to: B): A => A

  def modify(f: B => B): A => A

  def compose[C](other: Setter[B, C]): Setter[A, C] =
    new Setter[A, C] {
      def set(to: C): A => A        = self.modify(other.set(to))
      def modify(f: C => C): A => A = self.modify(other.modify(f))
    }

  def asTarget[C](implicit ev: B =:= C): Setter[A, C] =
    asInstanceOf[Setter[A, C]]
}

object Setter {
  def apply[A, B](_modify: (B => B) => (A => A)): Setter[A, B] = new Setter[A, B] {
    def set(to: B): A => A        = modify(_ => to)
    def modify(f: B => B): A => A = _modify(f)
  }
}
