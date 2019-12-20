package monocle

trait Getter[A, B] extends Fold[A, B] { self =>
  def get(from: A): B

  final override def toIterator(from: A): Iterator[B] =
    collection.Iterator.single(get(from))


  final override def map[C](f: B => C): Getter[A, C] =
    new Getter[A, C] {
      def get(from: A): C = f(self.get(from))
    }

  def compose[C](other: Getter[B, C]): Getter[A, C] =
    new Getter[A, C] {
      def get(from: A): C = other.get(self.get(from))
    }

  override def asTarget[C](implicit ev: B =:= C): Getter[A, C] =
    asInstanceOf[Getter[A, C]]
}

object Getter {
  def apply[A, B](_get: A => B): Getter[A, B] = new Getter[A, B] {
    def get(from: A): B = _get(from)
  }
}
