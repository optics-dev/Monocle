package monocle

trait Getter[From, To] extends Fold[From, To] { self =>
  def get(from: From): To

  override def toIterator(from: From): Iterator[To] =
    collection.Iterator.single(get(from))

  override def map[X](f: To => X): Getter[From, X] =
    new Getter[From, X] {
      def get(from: From): X = f(self.get(from))
    }

  def andThen[X](other: Getter[To, X]): Getter[From, X] =
    new Getter[From, X] {
      def get(from: From): X = other.get(self.get(from))
    }

  override def asTarget[X](implicit ev: To =:= X): Getter[From, X] =
    asInstanceOf[Getter[From, X]]
}

object Getter {
  def apply[From, To](_get: From => To): Getter[From, To] = new Getter[From, To] {
    def get(from: From): To = _get(from)
  }
}
