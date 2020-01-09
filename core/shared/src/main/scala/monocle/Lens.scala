package monocle

import monocle.function.{At, Field1, Field2, Field3, Field4, Field5, Field6}

trait Lens[From, To] extends Optional[From, To] with Getter[From, To] { self =>

  override def getOption(from: From): Option[To] = Some(get(from))

  override def modify(f: To => To): From => From = from => set(f(get(from)))(from)

  def compose[X](other: Lens[To, X]): Lens[From, X] = new Lens[From, X] {
    def get(from: From): X                       = other.get(self.get(from))
    def set(to: X): From => From                 = self.modify(other.set(to))
    override def modify(f: X => X): From => From = self.modify(other.modify(f))
  }

  override def asTarget[X](implicit ev: To =:= X): Lens[From, X] =
    asInstanceOf[Lens[From, X]]
}

object Lens {
  def apply[From, To](_get: From => To)(_set: (From, To) => From): Lens[From, To] = new Lens[From, To] {
    def get(from: From): To       = _get(from)
    def set(to: To): From => From = _set(_, to)
  }

  def at[From, Index, To](index: Index)(implicit ev: At.Aux[From, Index, To]): Lens[From, Option[To]] =
    ev.at(index)

  def first[From, To](implicit ev: Field1.Aux[From, To]): Lens[From, To] =
    ev.first

  def second[From, To](implicit ev: Field2.Aux[From, To]): Lens[From, To] =
    ev.second

  def third[From, To](implicit ev: Field3.Aux[From, To]): Lens[From, To] =
    ev.third

  def fourth[From, To](implicit ev: Field4.Aux[From, To]): Lens[From, To] =
    ev.fourth

  def fifth[From, To](implicit ev: Field5.Aux[From, To]): Lens[From, To] =
    ev.fifth

  def sixth[From, To](implicit ev: Field6.Aux[From, To]): Lens[From, To] =
    ev.sixth
}
