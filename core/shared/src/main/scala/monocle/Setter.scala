package monocle

trait Setter[From, To] { self =>
  def set(to: To): From => From

  def modify(f: To => To): From => From

  def andThen[X](other: Setter[To, X]): Setter[From, X] =
    new Setter[From, X] {
      def set(to: X): From => From        = self.modify(other.set(to))
      def modify(f: X => X): From => From = self.modify(other.modify(f))
    }

  def asTarget[X](implicit ev: To =:= X): Setter[From, X] =
    asInstanceOf[Setter[From, X]]
}

object Setter {
  def apply[From, To](_modify: (To => To) => (From => From)): Setter[From, To] = new Setter[From, To] {
    def set(to: To): From => From         = modify(_ => to)
    def modify(f: To => To): From => From = _modify(f)
  }
}
