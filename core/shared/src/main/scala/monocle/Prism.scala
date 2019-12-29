package monocle

import monocle.function._

trait Prism[A, B] extends Optional[A, B] { self =>
  def reverseGet(to: B): A

  final def set(to: B): A => A = _ => reverseGet(to)

  override def modify(f: B => B): A => A = a => getOption(a).fold(a)(reverseGet)

  override def asTarget[C](implicit ev: B =:= C): Prism[A, C] =
    asInstanceOf[Prism[A, C]]

  def compose[C](other: Prism[B, C]): Prism[A, C] = new Prism[A, C] {
    def getOption(from: A): Option[C] = self.getOption(from).flatMap(other.getOption)
    def reverseGet(to: C): A          = self.reverseGet(other.reverseGet(to))
  }

  ///////////////////////////////////
  // dot syntax for optics typeclass
  ///////////////////////////////////

  override def cons(implicit ev: Cons[B]): Prism[A, (ev.B, B)]  = compose(ev.cons)
  override def reverse(implicit ev: Reverse[B]): Prism[A, ev.B] = compose(ev.reverse)

  ///////////////////////////////////
  // dot syntax for standard types
  ///////////////////////////////////

  override def left[E, C](implicit ev: B =:= Either[E, C]): Prism[A, E] =
    asTarget[Either[E, C]].compose(Prism.left[E, C])
  override def right[E, C](implicit ev: B =:= Either[E, C]): Prism[A, C] =
    asTarget[Either[E, C]].compose(Prism.right[E, C])
  override def some[C](implicit ev: B =:= Option[C]): Prism[A, C] = asTarget[Option[C]].compose(Prism.some[C])
}

object Prism {
  def apply[A, B](_getOption: A => Option[B])(_reverseGet: B => A): Prism[A, B] = new Prism[A, B] {
    def reverseGet(to: B): A          = _reverseGet(to)
    def getOption(from: A): Option[B] = _getOption(from)
  }

  def partial[A, B](get: PartialFunction[A, B])(reverseGet: B => A): Prism[A, B] =
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
