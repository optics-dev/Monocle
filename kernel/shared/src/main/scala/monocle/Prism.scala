package monocle

trait Prism[A, B] extends Optional[A, B] { self =>
  def reverseGet(to: B): A

  final def set(to: B): A => A = _ => reverseGet(to)

  override def modify(f: B => B): A => A = a => getOption(a).fold(a)(reverseGet)

  def compose[C](other: Prism[B, C]): Prism[A, C] = new Prism[A, C] {
    def getOption(from: A): Option[C] = self.getOption(from).flatMap(other.getOption)
    def reverseGet(to: C): A = self.reverseGet(other.reverseGet(to))
  }
}

object Prism {
  def apply[A, B](_getOption: A => Option[B])(_reverseGet: B => A): Prism[A, B] = new Prism[A, B] {
    def reverseGet(to: B): A = _reverseGet(to)
    def getOption(from: A): Option[B] = _getOption(from)
  }

  def partial[A, B](get: PartialFunction[A, B])(reverseGet: B => A): Prism[A, B] =
    Prism(get.lift)(reverseGet)
}
