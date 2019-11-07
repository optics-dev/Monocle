package monocle

trait PPrism[S, T, A, B] extends POptional[S, T, A, B] { self =>

  def reverseGet(to: B): T

  final def set(to: B): S => T =
    _ => reverseGet(to)

  override def modify(f: A => B): S => T =
    s => getOrModify(s).fold(t => t, a => reverseGet(f(a)))

  override def asTarget[B](implicit ev: A =:= B): Prism[S, B] =
    asInstanceOf[Prism[S, B]]

  def compose[C, D](other: PPrism[A, B, C, D]): PPrism[S, T, C, D] =
    new PPrism[S, T, C, D] {
      def getOrModify(from: S): Either[T, C] =
        self.getOrModify(from).flatMap(a => other.getOrModify(a).left.map(self.set(_)(from)))
      override def getOption(from: S): Option[C] =
        self.getOption(from).flatMap(other.getOption)
      def reverseGet(to: D): T =
        self.reverseGet(other.reverseGet(to))
    }
}
