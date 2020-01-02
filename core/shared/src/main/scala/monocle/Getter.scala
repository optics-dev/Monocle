package monocle

import monocle.function._

trait Getter[-A, +B] extends Fold[A, B] { self =>
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

  override def asTarget[C](implicit ev: B <:< C): Getter[A, C] =
    asInstanceOf[Getter[A, C]]

  ///////////////////////////////////
  // dot syntax for optics typeclass
  ///////////////////////////////////

  override def _1[C >: B](implicit ev: Field1[C]): Getter[A, ev.B] = first(ev)
  override def _2[C >: B](implicit ev: Field2[C]): Getter[A, ev.B] = second(ev)
  override def _3[C >: B](implicit ev: Field3[C]): Getter[A, ev.B] = third(ev)
  override def _4[C >: B](implicit ev: Field4[C]): Getter[A, ev.B] = fourth(ev)
  override def _5[C >: B](implicit ev: Field5[C]): Getter[A, ev.B] = fifth(ev)
  override def _6[C >: B](implicit ev: Field6[C]): Getter[A, ev.B] = sixth(ev)

  override def first[C >: B](implicit ev: Field1[C]): Getter[A, ev.B]  = asTarget[C].compose(ev.first)
  override def second[C >: B](implicit ev: Field2[C]): Getter[A, ev.B] = compose(ev.second)
  override def third[C >: B](implicit ev: Field3[C]): Getter[A, ev.B]  = compose(ev.third)
  override def fourth[C >: B](implicit ev: Field4[C]): Getter[A, ev.B] = compose(ev.fourth)
  override def fifth[C >: B](implicit ev: Field5[C]): Getter[A, ev.B]  = compose(ev.fifth)
  override def sixth[C >: B](implicit ev: Field6[C]): Getter[A, ev.B]  = compose(ev.sixth)

  override def at[I, C >: B, D](i: I)(implicit ev: At.Aux[C, I, D]): Getter[A, Option[D]] = compose(ev.at(i))
  override def reverse[C >: B](implicit ev: Reverse[C]): Getter[A, ev.B]                  = compose(ev.reverse)
}

object Getter {
  def apply[A, B](_get: A => B): Getter[A, B] = new Getter[A, B] {
    def get(from: A): B = _get(from)
  }
}
