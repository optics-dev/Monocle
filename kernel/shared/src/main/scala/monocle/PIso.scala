package monocle

trait PIso[-S, +T, +A, -B] extends PLens[S, T, A, B] with PPrism[S, T, A, B] { self =>

  // def reverse: PIso[B, A, T, S] = ???

  final def compose[C, D](other: PIso[A, B, C, D]): PIso[S, T, C, D] =
    new PIso[S, T, C, D] {
      def get(from: S): C =
        other.get(self.get(from))
      def reverseGet(to: D): T =
        self.reverseGet(other.reverseGet(to))
    }
}
