package monocle

trait POptional[S, T, A, B] { self =>

  def getOrModify(from: S): Either[T, A]

  def set(to: B): S => T

  def getOption(from: S): Option[A] =
    getOrModify(from).toOption

  def modify(f: A => B): S => T =
    s => getOrModify(s).fold(t => t, a => set(f(a))(s))

  def asTarget[B](implicit ev: A =:= B): Optional[S, B] =
    asInstanceOf[Optional[S, B]]

  final def compose[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] =
    new POptional[S, T, C, D] {
      def getOrModify(from: S): Either[T, C] =
        self.getOrModify(from).flatMap(a => other.getOrModify(a).left.map(self.set(_)(from)))
      def set(to: D): S => T =
        self.modify(other.set(to))
      override def getOption(from: S): Option[C] =
        self.getOption(from).flatMap(other.getOption)
      override def modify(f: C => D): S => T =
        self.modify(other.modify(f))
    }

  def composeLens[C, D](other: PLens[A, B, C, D]): POptional[S, T, C, D] =
    compose(other)

  def composePrism[C, D](other: PPrism[A, B, C, D]): POptional[S, T, C, D] =
    compose(other)
}
