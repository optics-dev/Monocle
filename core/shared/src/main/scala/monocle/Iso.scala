package monocle

import monocle.function.Reverse

abstract class Iso[From, To] extends Lens[From, To] with Prism[From, To] { self =>
  override def modify(f: To => To): From => From =
    from => reverseGet(get(from))

  def compose[X](other: Iso[To, X]): Iso[From, X] = new Iso[From, X] {
    def get(from: From): X      = other.get(self.get(from))
    def reverseGet(to: X): From = self.reverseGet(other.reverseGet(to))
  }

  override def compose[X](other: Lens[To, X]): Lens[From, X] = new Lens[From, X] {
    def get(from: From): X       = other.get(self.get(from))
    def set(to: X): From => From = from => self.reverseGet(other.set(to)(self.get(from)))
  }

  override def compose[X](other: Prism[To, X]): Prism[From, X] = new Prism[From, X] {
    def getOption(from: From): Option[X] = other.getOption(self.get(from))
    def reverseGet(to: X): From          = self.reverseGet(other.reverseGet(to))
  }

  override def asTarget[X](implicit ev: To =:= X): Iso[From, X] =
    asInstanceOf[Iso[From, X]]
}

object Iso {
  def apply[From, To](_get: From => To)(_reverseGet: To => From): Iso[From, To] =
    new Iso[From, To] {
      def get(from: From): To      = _get(from)
      def reverseGet(to: To): From = _reverseGet(to)
    }

  def reverse[From, To](implicit ev: Reverse.Aux[From, To]): Iso[From, To] =
    ev.reverse

  def id[From]: Iso[From, From] =
    Iso[From, From](identity)(identity)
}
