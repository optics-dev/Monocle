package monocle

trait PLens[-S, +T, +A, -B] extends POptional[S, T, A, B] with PGetter[S, T, A, B] { self =>
  final override def getOrModify(from: S): Either[Nothing, A] =
    Right(get(from))

  final def compose[C, D](other: PLens[A, B, C, D]): PLens[S, T, C, D] =
    new PLens[S, T, C, D] {
      def get(from: S): C                    = other.get(self.get(from))
      def set(to: D): S => T                 = self.modify(other.set(to))
    }
}
