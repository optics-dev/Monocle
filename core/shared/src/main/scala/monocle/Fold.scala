package monocle

trait Fold[From, To] { self =>
  def toIterator(from: From): Iterator[To]

  def foldLeft[Z](zero: Z)(f: (Z, To) => Z): From => Z =
    from => {
      var acc = zero
      val it  = toIterator(from)
      while (it.hasNext) acc = f(acc, it.next())
      acc
    }

  def firstOption(from: From): Option[To] = {
    val it = toIterator(from)
    if (it.hasNext) Some(it.next())
    else None
  }

  def lastOption(from: From): Option[To] = {
    var acc: Option[To] = None
    val it              = toIterator(from)
    while (it.hasNext) acc = Some(it.next())
    acc
  }

  def toList(from: From): List[To] =
    toIterator(from).toList

  def find(predicate: To => Boolean): From => Option[To] =
    toIterator(_).find(predicate)

  def exist(predicate: To => Boolean): From => Boolean =
    toIterator(_).exists(predicate)

  def forAll(predicate: To => Boolean): From => Boolean =
    toIterator(_).forall(predicate)

  def length(from: From): Int =
    toIterator(from).length

  def isEmpty(from: From): Boolean =
    toIterator(from).hasNext

  def nonEmpty(from: From): Boolean =
    !isEmpty(from)

  def map[X](f: To => X): Fold[From, X] =
    new Fold[From, X] {
      def toIterator(from: From): Iterator[X] =
        self.toIterator(from).map(f)
    }

  def compose[X](other: Fold[To, X]): Fold[From, X] =
    new Fold[From, X] {
      def toIterator(from: From): Iterator[X] =
        self.toIterator(from).flatMap(other.toIterator)
    }

  def asTarget[X](implicit ev: To =:= X): Fold[From, X] =
    asInstanceOf[Fold[From, X]]
}

object Fold {
  def apply[From, To](_toIterator: From => Iterator[To]): Fold[From, To] =
    new Fold[From, To] {
      def toIterator(from: From): Iterator[To] =
        _toIterator(from)
    }

  def list[A]: Fold[List[A], A]     = apply(_.iterator)
  def vector[A]: Fold[Vector[A], A] = apply(_.iterator)
}
