package monocle

trait Getter[A, B] { self =>
  def get(from: A): B

  def compose[C](other: Getter[B, C]): Getter[A, C] =
    new Getter[A, C] {
      def get(from: A): C = other.get(self.get(from))
    }

  def asTarget[C](implicit ev: B =:= C): Getter[A, C] =
    asInstanceOf[Getter[A, C]]
}

object Getter {
  def apply[A, B](_get: A => B): Getter[A, B] = new Getter[A, B] {
    def get(from: A): B = _get(from)
  }
}
