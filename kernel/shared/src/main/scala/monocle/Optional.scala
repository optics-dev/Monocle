package monocle

import monocle.function.{Cons, Index, Possible}

trait Optional[A, B] { self =>
  def getOption(from: A): Option[B]
  def set(to: B): A => A

  def modify(f: B => B): A => A = a => getOption(a).fold(a)(set(_)(a))

  def asTarget[C](implicit ev: B =:= C): Optional[A, C] =
    asInstanceOf[Optional[A, C]]

  final def compose[C](other: Optional[B, C]): Optional[A, C] = new Optional[A, C] {
    def getOption(from: A): Option[C]      = self.getOption(from).flatMap(other.getOption)
    def set(to: C): A => A                 = self.modify(other.set(to))
    override def modify(f: C => C): A => A = self.modify(other.modify(f))
  }

  def composeLens[C](other: Lens[B, C]): Optional[A, C]   = compose(other)
  def composePrism[C](other: Prism[B, C]): Optional[A, C] = compose(other)
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
