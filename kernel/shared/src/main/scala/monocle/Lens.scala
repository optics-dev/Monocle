package monocle

import monocle.function.{Field1, Field2}

trait Lens[A, B] extends Optional[A, B] { self =>
  def get(from: A): B

  final def getOption(from: A): Option[B] = Some(get(from))

  override def modify(f: B => B): A => A = from => set(f(get(from)))(from)

  def compose[C](other: Lens[B, C]): Lens[A, C] = new Lens[A, C] {
    def get(from: A): C = other.get(self.get(from))
    def set(to: C): A => A = self.modify(other.set(to))
    override def modify(f: C => C): A => A = self.modify(other.modify(f))
  }
}

object Lens {
  def apply[A, B](_get: A => B)(_set: (A, B) => A): Lens[A, B] = new Lens[A, B] {
    def get(from: A): B = _get(from)
    def set(to: B): A => A = _set(_, to)
  }

  def first[S, A](implicit ev: Field1.Aux[S, A]): Lens[S, A] = ev.first
  def second[S, A](implicit ev: Field2.Aux[S, A]): Lens[S, A] = ev.second
}
