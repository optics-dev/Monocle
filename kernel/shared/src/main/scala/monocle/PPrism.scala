package monocle

trait PPrism[-S, +T, +A, -B] extends POptional[S, T, A, B] { self =>

  def reverseGet(to: B): T

  final def set(to: B): S => T =
    _ => reverseGet(to)

  final def compose[C, D](other: PPrism[A, B, C, D]): PPrism[S, T, C, D] =
    new PPrism[S, T, C, D] {
      def getOrModify(from: S): Either[T, C] =
        self.getOrModify(from).flatMap(a => other.getOrModify(a).left.map(self.set(_)(from)))
      def reverseGet(to: D): T =
        self.reverseGet(other.reverseGet(to))
    }
}
