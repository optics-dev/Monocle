package monocle

trait PLens[S, T, A, B] extends POptional[S, T, A, B] { self =>

  def get(from: S): A

  final override def getOrModify(from: S): Either[Nothing, A] =
    Right(get(from))

  final override def getOption(from: S): Option[A] =
    Some(get(from))

  override def modify(f: A => B): S => T =
    from => set(f(get(from)))(from)

  override def asTarget[B](implicit ev: A =:= B): Lens[S, B] =
    asInstanceOf[Lens[S, B]]

  def compose[C, D](other: PLens[A, B, C, D]): PLens[S, T, C, D] =
    new PLens[S, T, C, D] {
      def get(from: S): C                    = other.get(self.get(from))
      def set(to: D): S => T                 = self.modify(other.set(to))
      override def modify(f: C => D): S => T = self.modify(other.modify(f))
    }
}
