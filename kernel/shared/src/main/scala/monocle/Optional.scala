package monocle

import monocle.function.{Cons, Index, Possible}

object Optional {
  def apply[S, A](_getOption: S => Option[A])(_set: (S, A) => S): Optional[S, A] =
    POptional[S, S, A, A](s => _getOption(s).fold[Either[S, A]](Left(s))(Right(_)))(_set)

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
