package monocle

import monocle.function._

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

  ///////////////////////////////////
  // dot syntax for optics typeclass
  ///////////////////////////////////

  override def _1(implicit ev: Field1[B]): Getter[A, ev.B] = first(ev)
  override def _2(implicit ev: Field2[B]): Getter[A, ev.B] = second(ev)
  override def _3(implicit ev: Field3[B]): Getter[A, ev.B] = third(ev)
  override def _4(implicit ev: Field4[B]): Getter[A, ev.B] = fourth(ev)
  override def _5(implicit ev: Field5[B]): Getter[A, ev.B] = fifth(ev)
  override def _6(implicit ev: Field6[B]): Getter[A, ev.B] = sixth(ev)

  override def first(implicit ev: Field1[B]): Getter[A, ev.B]  = compose(ev.first)
  override def second(implicit ev: Field2[B]): Getter[A, ev.B] = compose(ev.second)
  override def third(implicit ev: Field3[B]): Getter[A, ev.B]  = compose(ev.third)
  override def fourth(implicit ev: Field4[B]): Getter[A, ev.B] = compose(ev.fourth)
  override def fifth(implicit ev: Field5[B]): Getter[A, ev.B]  = compose(ev.fifth)
  override def sixth(implicit ev: Field6[B]): Getter[A, ev.B]  = compose(ev.sixth)

  override def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): Getter[A, Option[C]] = compose(ev.at(i))
  override def reverse(implicit ev: Reverse[B]): Getter[A, ev.B]                  = compose(ev.reverse)
}

object Getter {
  def apply[A, B](_get: A => B): Getter[A, B] = new Getter[A, B] {
    def get(from: A): B = _get(from)
  }
}
