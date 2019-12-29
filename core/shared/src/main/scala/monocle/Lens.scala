package monocle

import monocle.function._

trait Lens[A, B] extends Optional[A, B] with Getter[A, B] { self =>

  final override def getOption(from: A): Option[B] = Some(get(from))

  override def modify(f: B => B): A => A = from => set(f(get(from)))(from)

  override def asTarget[C](implicit ev: B =:= C): Lens[A, C] =
    asInstanceOf[Lens[A, C]]

  def compose[C](other: Lens[B, C]): Lens[A, C] = new Lens[A, C] {
    def get(from: A): C                    = other.get(self.get(from))
    def set(to: C): A => A                 = self.modify(other.set(to))
    override def modify(f: C => C): A => A = self.modify(other.modify(f))
  }

  ///////////////////////////////////
  // dot syntax for optics typeclass
  ///////////////////////////////////

  override def _1(implicit ev: Field1[B]): Lens[A, ev.B] = first(ev)
  override def _2(implicit ev: Field2[B]): Lens[A, ev.B] = second(ev)
  override def _3(implicit ev: Field3[B]): Lens[A, ev.B] = third(ev)
  override def _4(implicit ev: Field4[B]): Lens[A, ev.B] = fourth(ev)
  override def _5(implicit ev: Field5[B]): Lens[A, ev.B] = fifth(ev)
  override def _6(implicit ev: Field6[B]): Lens[A, ev.B] = sixth(ev)

  override def first(implicit ev: Field1[B]): Lens[A, ev.B]  = compose(ev.first)
  override def second(implicit ev: Field2[B]): Lens[A, ev.B] = compose(ev.second)
  override def third(implicit ev: Field3[B]): Lens[A, ev.B]  = compose(ev.third)
  override def fourth(implicit ev: Field4[B]): Lens[A, ev.B] = compose(ev.fourth)
  override def fifth(implicit ev: Field5[B]): Lens[A, ev.B]  = compose(ev.fifth)
  override def sixth(implicit ev: Field6[B]): Lens[A, ev.B]  = compose(ev.sixth)

  override def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): Lens[A, Option[C]] = compose(ev.at(i))
  override def reverse(implicit ev: Reverse[B]): Lens[A, ev.B]                  = compose(ev.reverse)

  ///////////////////////////////////
  // dot syntax for standard types
  ///////////////////////////////////

  override def left[E, C](implicit ev: B =:= Either[E, C]): Optional[A, E] =
    asTarget[Either[E, C]].compose(Prism.left[E, C])
  override def right[E, C](implicit ev: B =:= Either[E, C]): Optional[A, C] =
    asTarget[Either[E, C]].compose(Prism.right[E, C])
  override def some[C](implicit ev: B =:= Option[C]): Optional[A, C] = asTarget[Option[C]].compose(Prism.some[C])
}

object Lens {
  def apply[A, B](_get: A => B)(_set: (A, B) => A): Lens[A, B] = new Lens[A, B] {
    def get(from: A): B    = _get(from)
    def set(to: B): A => A = _set(_, to)
  }

  def at[S, I, A](index: I)(implicit ev: At.Aux[S, I, A]): Lens[S, Option[A]] =
    ev.at(index)

  def first[S, A](implicit ev: Field1.Aux[S, A]): Lens[S, A] =
    ev.first

  def second[S, A](implicit ev: Field2.Aux[S, A]): Lens[S, A] =
    ev.second

  def third[S, A](implicit ev: Field3.Aux[S, A]): Lens[S, A] =
    ev.third

  def fourth[S, A](implicit ev: Field4.Aux[S, A]): Lens[S, A] =
    ev.fourth

  def fifth[S, A](implicit ev: Field5.Aux[S, A]): Lens[S, A] =
    ev.fifth

  def sixth[S, A](implicit ev: Field6.Aux[S, A]): Lens[S, A] =
    ev.sixth
}
