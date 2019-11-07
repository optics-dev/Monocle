package monocle

trait POptional[-S, +T, +A, -B] extends PSetter[S, T, A, B] { self =>

  def getOrModify(from: S): Either[T, A]

  def set(to: B): S => T

  final def fold[Z](zero: Z, combine: (Z, Z) => Z)(f: A => Z)(s: S): Z =
    getOrModify(s).fold(_ => zero, a => f(a))

  final def getOption(from: S): Option[A] =
    getOrModify(from).toOption

  final def modify(f: A => B): S => T =
    s => getOrModify(s).fold(t => t, a => set(f(a))(s))

  final def compose[C, D](other: POptional[A, B, C, D]): POptional[S, T, C, D] =
    new POptional[S, T, C, D] {
      def getOrModify(from: S): Either[T, C] =
        self.getOrModify(from).flatMap(a => other.getOrModify(a).left.map(self.set(_)(from)))
      def set(to: D): S => T =
        self.modify(other.set(to))
    }
}
