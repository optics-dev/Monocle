package monocle

import monocle.function._

trait Optional[A, B] extends Fold[A, B] with Setter[A, B] { self =>
  def getOption(from: A): Option[B]

  def modify(f: B => B): A => A = a => getOption(a).fold(a)(set(_)(a))

  override def toIterator(from: A): Iterator[B] =
    getOption(from).iterator

  override def asTarget[C](implicit ev: B =:= C): Optional[A, C] =
    asInstanceOf[Optional[A, C]]

  final def compose[C](other: Optional[B, C]): Optional[A, C] = new Optional[A, C] {
    def getOption(from: A): Option[C]      = self.getOption(from).flatMap(other.getOption)
    def set(to: C): A => A                 = self.modify(other.set(to))
    override def modify(f: C => C): A => A = self.modify(other.modify(f))
  }

  def composeLens[C](other: Lens[B, C]): Optional[A, C]   = compose(other)
  def composePrism[C](other: Prism[B, C]): Optional[A, C] = compose(other)

  ///////////////////////////////////
  // dot syntax for optics typeclass
  ///////////////////////////////////

  override def _1(implicit ev: Field1[B]): Optional[A, ev.B] = first(ev)
  override def _2(implicit ev: Field2[B]): Optional[A, ev.B] = second(ev)
  override def _3(implicit ev: Field3[B]): Optional[A, ev.B] = third(ev)
  override def _4(implicit ev: Field4[B]): Optional[A, ev.B] = fourth(ev)
  override def _5(implicit ev: Field5[B]): Optional[A, ev.B] = fifth(ev)
  override def _6(implicit ev: Field6[B]): Optional[A, ev.B] = sixth(ev)

  override def first(implicit ev: Field1[B]): Optional[A, ev.B]  = compose(ev.first)
  override def second(implicit ev: Field2[B]): Optional[A, ev.B] = compose(ev.second)
  override def third(implicit ev: Field3[B]): Optional[A, ev.B]  = compose(ev.third)
  override def fourth(implicit ev: Field4[B]): Optional[A, ev.B] = compose(ev.fourth)
  override def fifth(implicit ev: Field5[B]): Optional[A, ev.B]  = compose(ev.fifth)
  override def sixth(implicit ev: Field6[B]): Optional[A, ev.B]  = compose(ev.sixth)

  override def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): Optional[A, Option[C]] = compose(ev.at(i))
  override def cons(implicit ev: Cons[B]): Optional[A, (ev.B, B)]                   = compose(ev.cons)
  override def headOption(implicit ev: Cons[B]): Optional[A, ev.B]                  = compose(ev.headOption)
  override def tailOption(implicit ev: Cons[B]): Optional[A, B]                     = compose(ev.tailOption)
  override def index[I, C](i: I)(implicit ev: Index.Aux[B, I, C]): Optional[A, C]   = compose(ev.index(i))
  override def possible(implicit ev: Possible[B]): Optional[A, ev.B]                = compose(ev.possible)
  override def reverse(implicit ev: Reverse[B]): Optional[A, ev.B]                  = compose(ev.reverse)

  ///////////////////////////////////
  // dot syntax for standard types
  ///////////////////////////////////

  override def left[E, C](implicit ev: B =:= Either[E, C]): Optional[A, E] =
    asTarget[Either[E, C]].compose(Prism.left[E, C])
  override def right[E, C](implicit ev: B =:= Either[E, C]): Optional[A, C] =
    asTarget[Either[E, C]].compose(Prism.right[E, C])
  override def some[C](implicit ev: B =:= Option[C]): Optional[A, C] = asTarget[Option[C]].compose(Prism.some[C])
}

object Optional {
  def apply[A, B](_getOption: A => Option[B])(_set: (A, B) => A): Optional[A, B] = new Optional[A, B] {
    def getOption(from: A): Option[B] = _getOption(from)
    def set(to: B): A => A            = _set(_, to)
  }

  def void[S, A]: Optional[S, A] =
    Optional[S, A](_ => None)((a, _) => a)

  def headOption[S, A](implicit ev: Cons.Aux[S, A]): Optional[S, A] =
    ev.headOption

  def index[S, I, A](index: I)(implicit ev: Index.Aux[S, I, A]): Optional[S, A] =
    ev.index(index)

  def tailOption[S](implicit ev: Cons[S]): Optional[S, S] =
    ev.tailOption

  def possible[A, B](implicit ev: Possible.Aux[A, B]): Optional[A, B] =
    ev.possible
}
