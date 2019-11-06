package monocle

import monocle.function.{Cons, Index, Possible}

object Optional {
  def apply[S, A](_getOption: S => Option[A])(_set: (S, A) => S): Optional[S, A] =
    new Optional[S, A] {
      def getOrModify(from: S): Either[S, A] =
        _getOption(from).fold[Either[S, A]](Left(from))(Right(_))
      def set(to: A): S => S =
        _set(_, to)
    }

  def void[S, A]: Optional[S, A] =
    Optional[S, A](_ => None)((a, _) => a)

  def headOption[S, A](implicit ev: Cons.Aux[S, A]): Optional[S, A] =
    ev.headOption

  def index[S, I, A](index: I)(implicit ev: Index.Aux[S, I, A]): Optional[S, A] =
    ev.index(index)

  def tailOption[S](implicit ev: Cons[S]): Optional[S, S] =
    ev.tailOption

  def possible[S, A](implicit ev: Possible.Aux[S, A]): Optional[S, A] =
    ev.possible
}
