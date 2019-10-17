package monocle

import monocle.function.Cons

trait Prism[A, B] extends Optional[A, B] { self =>
  def reverseGet(to: B): A

  final def set(to: B): A => A = _ => reverseGet(to)

  override def modify(f: B => B): A => A = a => getOption(a).fold(a)(reverseGet)

  def compose[C](other: Prism[B, C]): Prism[A, C] = new Prism[A, C] {
    def getOption(from: A): Option[C] = self.getOption(from).flatMap(other.getOption)
    def reverseGet(to: C): A = self.reverseGet(other.reverseGet(to))
  }
}

object Prism {
  def apply[A, B](_getOption: A => Option[B])(_reverseGet: B => A): Prism[A, B] = new Prism[A, B] {
    def reverseGet(to: B): A = _reverseGet(to)
    def getOption(from: A): Option[B] = _getOption(from)
  }

  def partial[A, B](get: PartialFunction[A, B])(reverseGet: B => A): Prism[A, B] =
    Prism(get.lift)(reverseGet)

  def cons[S, A](implicit ev: Cons.Aux[S, A]): Prism[S, (A, S)] =
    ev.cons

  def some[A]: Prism[Option[A], A] =
    partial[Option[A], A]{ case Some(a) => a }(Some(_))

  def none[A]: Prism[Option[A], Unit] =
    partial[Option[A], Unit]{ case None => () }(_ => None)

  def left[E, A]: Prism[Either[E, A], E] =
    partial[Either[E, A], E]{ case Left(e) => e }(Left(_))

  def right[E, A]: Prism[Either[E, A], A] =
    partial[Either[E, A], A]{ case Right(e) => e }(Right(_))
}
