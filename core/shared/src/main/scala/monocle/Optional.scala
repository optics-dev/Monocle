package monocle

import monocle.function.{Cons, Index, Possible}

trait Optional[From, To] extends Fold[From, To] with Setter[From, To] { self =>
  def getOption(from: From): Option[To]

  def modify(f: To => To): From => From = a => getOption(a).fold(a)(set(_)(a))

  override def toIterator(from: From): Iterator[To] =
    getOption(from).iterator

  def andThen[X](other: Optional[To, X]): Optional[From, X] = new Optional[From, X] {
    def getOption(from: From): Option[X]         = self.getOption(from).flatMap(other.getOption)
    def set(to: X): From => From                 = self.modify(other.set(to))
    override def modify(f: X => X): From => From = self.modify(other.modify(f))
  }

  def andThenLens[X](other: Lens[To, X]): Optional[From, X]   = andThen(other)
  def andThenPrism[X](other: Prism[To, X]): Optional[From, X] = andThen(other)

  override def asTarget[X](implicit ev: To =:= X): Optional[From, X] =
    asInstanceOf[Optional[From, X]]
}

object Optional {
  def apply[From, To](_getOption: From => Option[To])(_set: (From, To) => From): Optional[From, To] =
    new Optional[From, To] {
      def getOption(from: From): Option[To] = _getOption(from)
      def set(to: To): From => From         = _set(_, to)
    }

  def void[From, To]: Optional[From, To] =
    Optional[From, To](_ => None)((a, _) => a)

  def headOption[From, To](implicit ev: Cons.Aux[From, To]): Optional[From, To] =
    ev.headOption

  def tailOption[From](implicit ev: Cons[From]): Optional[From, From] =
    ev.tailOption

  def index[From, Index, To](index: Index)(implicit ev: Index.Aux[From, Index, To]): Optional[From, To] =
    ev.index(index)

  def possible[From, To](implicit ev: Possible.Aux[From, To]): Optional[From, To] =
    ev.possible
}
