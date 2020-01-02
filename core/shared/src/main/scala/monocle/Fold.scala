package monocle

import monocle.function._

trait Fold[-A, +B] { self =>
  def toIterator(from: A): Iterator[B]

  def foldLeft[Z](zero: Z)(f: (Z, B) => Z): A => Z =
    from => {
      var acc = zero
      val it  = toIterator(from)
      while (it.hasNext) acc = f(acc, it.next())
      acc
    }

  final def firstOption(from: A): Option[B] = {
    val it = toIterator(from)
    if (it.hasNext) Some(it.next())
    else None
  }

  final def lastOption(from: A): Option[B] = {
    var acc: Option[B] = None
    val it             = toIterator(from)
    while (it.hasNext) acc = Some(it.next())
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

  def asTarget[C](implicit ev: B <:< C): Fold[A, C] =
    asInstanceOf[Fold[A, C]]

  ///////////////////////////////////
  // dot syntax for optics typeclass
  ///////////////////////////////////

  def _1[C >: B](implicit ev: Field1[C]): Fold[A, ev.B] = first(ev)
  def _2[C >: B](implicit ev: Field2[C]): Fold[A, ev.B] = second(ev)
  def _3[C >: B](implicit ev: Field3[C]): Fold[A, ev.B] = third(ev)
  def _4[C >: B](implicit ev: Field4[C]): Fold[A, ev.B] = fourth(ev)
  def _5[C >: B](implicit ev: Field5[C]): Fold[A, ev.B] = fifth(ev)
  def _6[C >: B](implicit ev: Field6[C]): Fold[A, ev.B] = sixth(ev)

  def first[C >: B](implicit ev: Field1[C]): Fold[A, ev.B]  = asTarget[C].compose(ev.first)
  def second[C >: B](implicit ev: Field2[C]): Fold[A, ev.B] = compose(ev.second)
  def third[C >: B](implicit ev: Field3[C]): Fold[A, ev.B]  = compose(ev.third)
  def fourth[C >: B](implicit ev: Field4[C]): Fold[A, ev.B] = compose(ev.fourth)
  def fifth[C >: B](implicit ev: Field5[C]): Fold[A, ev.B]  = compose(ev.fifth)
  def sixth[C >: B](implicit ev: Field6[C]): Fold[A, ev.B]  = compose(ev.sixth)

  def at[I, C >: B, D](i: I)(implicit ev: At.Aux[C, I, D]): Fold[A, Option[D]] = compose(ev.at(i))
  def cons[C >: B](implicit ev: Cons[C]): Fold[A, (ev.B, C)]                   = compose(ev.cons)
  def headOption[C >: B](implicit ev: Cons[C]): Fold[A, ev.B]                  = compose(ev.headOption)
  def tailOption[C >: B](implicit ev: Cons[C]): Fold[A, C]                     = compose(ev.tailOption)
  def index[I, C >: B, D](i: I)(implicit ev: Index.Aux[C, I, D]): Fold[A, D]   = compose(ev.index(i))
  def possible[C >: B](implicit ev: Possible[C]): Fold[A, ev.B]                = compose(ev.possible)
  def reverse[C >: B](implicit ev: Reverse[C]): Fold[A, ev.B]                  = compose(ev.reverse)

  ///////////////////////////////////
  // dot syntax for standard types
  ///////////////////////////////////

  def left[E, C](implicit ev: B <:< Either[E, C]): Fold[A, E]  = asTarget[Either[E, C]].compose(Prism.left[E, C])
  def right[E, C](implicit ev: B <:< Either[E, C]): Fold[A, C] = asTarget[Either[E, C]].compose(Prism.right[E, C])
  def some[C](implicit ev: B <:< Option[C]): Fold[A, C]        = asTarget[Option[C]].compose(Prism.some[C])
}

object Fold {
  def apply[A, B](_toIterator: A => Iterator[B]): Fold[A, B] =
    new Fold[A, B] {
      def toIterator(from: A): Iterator[B] =
        _toIterator(from)
    }

  def list[A]: Fold[List[A], A]     = apply(_.iterator)
  def vector[A]: Fold[Vector[A], A] = apply(_.iterator)
}
