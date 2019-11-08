package monocle

import monocle.function.Cons

object Prism {
  def apply[S, A](_getOption: S => Option[A])(_reverseGet: A => S): Prism[S, A] =
    PPrism[S, S, A, A](s => _getOption(s).fold[Either[S, A]](Left(s))(Right(_)))(_reverseGet)

  def partial[S, A](get: PartialFunction[S, A])(reverseGet: A => S): Prism[S, A] =
    Prism(get.lift)(reverseGet)

  def cons[S, A](implicit ev: Cons.Aux[S, A]): Prism[S, (A, S)] =
    ev.cons

  def some[A]: Prism[Option[A], A] =
    partial[Option[A], A] { case Some(a) => a }(Some(_))

  def none[A]: Prism[Option[A], Unit] =
    partial[Option[A], Unit] { case None => () }(_ => None)

  def left[E, A]: Prism[Either[E, A], E] =
    partial[Either[E, A], E] { case Left(e) => e }(Left(_))

  def right[E, A]: Prism[Either[E, A], A] =
    partial[Either[E, A], A] { case Right(e) => e }(Right(_))
}
