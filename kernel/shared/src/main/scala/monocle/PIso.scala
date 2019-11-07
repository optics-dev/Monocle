package monocle

abstract class PIso[S, T, A, B] extends PLens[S, T, A, B] with PPrism[S, T, A, B] { self =>

  override def modify(f: A => B): S => T =
    from => reverseGet(f(get(from)))

  override def asTarget[B](implicit ev: A =:= B): Iso[S, B] =
    asInstanceOf[Iso[S, B]]

  def compose[C, D](other: PIso[A, B, C, D]): PIso[S, T, C, D] =
    new PIso[S, T, C, D] {
      def get(from: S): C =
        other.get(self.get(from))
      def reverseGet(to: D): T =
        self.reverseGet(other.reverseGet(to))
    }

  override def compose[C, D](other: PLens[A, B, C, D]): PLens[S, T, C, D] =
    new PLens[S, T, C, D] {
      def get(from: S): C =
        other.get(self.get(from))
      def set(to: D): S => T =
        from => self.reverseGet(other.set(to)(self.get(from)))
    }

  override def compose[C, D](other: PPrism[A, B, C, D]): PPrism[S, T, C, D] =
    new PPrism[S, T, C, D] {
      def getOrModify(from: S): Either[T, C] =
        self.getOrModify(from).flatMap(a => other.getOrModify(a).left.map(self.set(_)(from)))
      override def getOption(from: S): Option[C] =
        other.getOption(self.get(from))
      def reverseGet(to: D): T =
        self.reverseGet(other.reverseGet(to))
    }
}
