package monocle

import monocle.function.Cons

trait Prism[From, To] extends Optional[From, To] { self =>
  def reverseGet(to: To): From

  def set(to: To): From => From = _ => reverseGet(to)

  override def modify(f: To => To): From => From = a => getOption(a).fold(a)(reverseGet)

  override def asTarget[C](implicit ev: To =:= C): Prism[From, C] =
    asInstanceOf[Prism[From, C]]

  def andThen[C](other: Prism[To, C]): Prism[From, C] = new Prism[From, C] {
    def getOption(from: From): Option[C] = self.getOption(from).flatMap(other.getOption)
    def reverseGet(to: C): From          = self.reverseGet(other.reverseGet(to))
  }
}

object Prism {
  def apply[From, To](_getOption: From => Option[To])(_reverseGet: To => From): Prism[From, To] = new Prism[From, To] {
    def reverseGet(to: To): From          = _reverseGet(to)
    def getOption(from: From): Option[To] = _getOption(from)
  }

  def partial[From, To](get: PartialFunction[From, To])(reverseGet: To => From): Prism[From, To] =
    Prism(get.lift)(reverseGet)

  def some[A]: Prism[Option[A], A] =
    partial[Option[A], A] { case Some(a) => a }(Some(_))

  def none[A]: Prism[Option[A], Unit] =
    partial[Option[A], Unit] { case None => () }(_ => None)

  def left[E, A]: Prism[Either[E, A], E] =
    partial[Either[E, A], E] { case Left(e) => e }(Left(_))

  def right[E, A]: Prism[Either[E, A], A] =
    partial[Either[E, A], A] { case Right(e) => e }(Right(_))

  def cons[From, To](implicit ev: Cons.Aux[From, To]): Prism[From, (To, From)] =
    ev.cons
}
