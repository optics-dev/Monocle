package monocle.function

import monocle.{Iso, Optional}

import scala.annotation.implicitNotFound

/**
 * Typeclass that defines an [[Optional]] from an `S` to an `A` at an index `I`
 * [[Index]] is less powerful than [[At]] as it cannot create or delete value
 * @tparam S source of [[Optional]]
 * @tparam I index
 * @tparam A target of [[Optional]], `A` is supposed to be unique for a given pair `(S, I)`
 */
@implicitNotFound("Could not find an instance of Index[${S},${I},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
abstract class Index[S, I, A] extends Serializable {
  def index(i: I): Optional[S, A]
}

object Index extends IndexFunctions {
  /** lift an instance of [[Index]] using an [[Iso]] */
  def fromIso[S, A, I, B](iso: Iso[S, A])(implicit ev: Index[A, I, B]): Index[S, I, B] = new Index[S, I, B] {
    override def index(i: I): Optional[S, B] =
      iso composeOptional ev.index(i)
  }
}

trait IndexFunctions {
  def index[S, I, A](i: I)(implicit ev: Index[S, I, A]): Optional[S, A] = ev.index(i)

  def atIndex[S, I, A](implicit ev: At[S, I, Option[A]]) = new Index[S, I, A] {
    def index(i: I) = ev.at(i) composePrism monocle.std.option.some
  }
}


