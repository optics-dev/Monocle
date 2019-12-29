package monocle

import monocle.function._

trait Setter[A, B] { self =>
  def set(to: B): A => A

  def modify(f: B => B): A => A

  def compose[C](other: Setter[B, C]): Setter[A, C] =
    new Setter[A, C] {
      def set(to: C): A => A        = self.modify(other.set(to))
      def modify(f: C => C): A => A = self.modify(other.modify(f))
    }

  def asTarget[C](implicit ev: B =:= C): Setter[A, C] =
    asInstanceOf[Setter[A, C]]

  ///////////////////////////////////
  // dot syntax for optics typeclass
  ///////////////////////////////////

  def _1(implicit ev: Field1[B]): Setter[A, ev.B] = first(ev)
  def _2(implicit ev: Field2[B]): Setter[A, ev.B] = second(ev)
  def _3(implicit ev: Field3[B]): Setter[A, ev.B] = third(ev)
  def _4(implicit ev: Field4[B]): Setter[A, ev.B] = fourth(ev)
  def _5(implicit ev: Field5[B]): Setter[A, ev.B] = fifth(ev)
  def _6(implicit ev: Field6[B]): Setter[A, ev.B] = sixth(ev)

  def first(implicit ev: Field1[B]): Setter[A, ev.B]  = compose(ev.first)
  def second(implicit ev: Field2[B]): Setter[A, ev.B] = compose(ev.second)
  def third(implicit ev: Field3[B]): Setter[A, ev.B]  = compose(ev.third)
  def fourth(implicit ev: Field4[B]): Setter[A, ev.B] = compose(ev.fourth)
  def fifth(implicit ev: Field5[B]): Setter[A, ev.B]  = compose(ev.fifth)
  def sixth(implicit ev: Field6[B]): Setter[A, ev.B]  = compose(ev.sixth)

  def at[I, C](i: I)(implicit ev: At.Aux[B, I, C]): Setter[A, Option[C]] = compose(ev.at(i))
  def cons(implicit ev: Cons[B]): Setter[A, (ev.B, B)]                   = compose(ev.cons)
  def headOption(implicit ev: Cons[B]): Setter[A, ev.B]                  = compose(ev.headOption)
  def tailOption(implicit ev: Cons[B]): Setter[A, B]                     = compose(ev.tailOption)
  def index[I, C](i: I)(implicit ev: Index.Aux[B, I, C]): Setter[A, C]   = compose(ev.index(i))
  def possible(implicit ev: Possible[B]): Setter[A, ev.B]                = compose(ev.possible)
  def reverse(implicit ev: Reverse[B]): Setter[A, ev.B]                  = compose(ev.reverse)

  ///////////////////////////////////
  // dot syntax for standard types
  ///////////////////////////////////

  def left[E, C](implicit ev: B =:= Either[E, C]): Setter[A, E]  = asTarget[Either[E, C]].compose(Prism.left[E, C])
  def right[E, C](implicit ev: B =:= Either[E, C]): Setter[A, C] = asTarget[Either[E, C]].compose(Prism.right[E, C])
  def some[C](implicit ev: B =:= Option[C]): Setter[A, C]        = asTarget[Option[C]].compose(Prism.some[C])
}

object Setter {
  def apply[A, B](_modify: (B => B) => (A => A)): Setter[A, B] = new Setter[A, B] {
    def set(to: B): A => A        = modify(_ => to)
    def modify(f: B => B): A => A = _modify(f)
  }
}
