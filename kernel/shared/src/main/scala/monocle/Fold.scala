package monocle

trait Fold[A, B] { self =>
  def toIterator(from: A): Iterator[B]

  def foldLeft[Z](zero: Z)(f: (Z, B) => Z): A => Z =
    from => {
      var acc = zero
      val it = toIterator(from)
      while(it.hasNext) acc = f(acc, it.next())
      acc
  }

  final def firstOption(from: A): Option[B] =  {
    val it = toIterator(from)
    if(it.hasNext) Some(it.next())
    else None
  }

  final def lastOption(from: A): Option[B] =  {
    var acc: Option[B] = None
    val it = toIterator(from)
    while(it.hasNext) acc = Some(it.next())
    acc
  }

  final def toList(from: A): List[B] =
    toIterator(from).toList

  final def find(predicate: B => Boolean): A => Option[B] =
    toIterator(_).find(predicate)

  final def exist(predicate: B => Boolean): A => Boolean =
    toIterator(_).exists(predicate)

  final def forAll(predicate: B => Boolean): A => Boolean =
    toIterator(_).forall(predicate)

  final def length(from: A): Int =
    toIterator(from).length

  final def isEmpty(from: A): Boolean =
    toIterator(from).hasNext

  final def nonEmpty(from: A): Boolean =
    !isEmpty(from)

  def map[C](f: B => C): Fold[A, C] =
    new Fold[A, C] {
      def toIterator(from: A): Iterator[C] =
        self.toIterator(from).map(f)
    }

  def compose[C](other: Fold[B, C]): Fold[A, C] =
    new Fold[A, C] {
      def toIterator(from: A): Iterator[C] =
        self.toIterator(from).flatMap(other.toIterator)
    }

  def asTarget[C](implicit ev: B =:= C): Fold[A, C] =
    asInstanceOf[Fold[A, C]]
}

object Fold {
  def apply[A, B](_toIterator: A => Iterator[B]): Fold[A, B] =
    new Fold[A, B] {
      def toIterator(from: A): Iterator[B] =
        _toIterator(from)
    }

  def list[A]: Fold[List[A], A] = apply(_.iterator)
  def vector[A]: Fold[Vector[A], A] = apply(_.iterator)
}