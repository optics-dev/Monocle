package monocle.function

import monocle.Optional

import scala.annotation.implicitNotFound

@implicitNotFound("Could not find an instance of Index[${S},${I},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Index[S, I, A] {

  /**
   * Creates a Traversal from S to 0 or 1 A
   * index is less powerful than at as we can only modify an index that already exist
   */
  def index(i: I): Optional[S, A]

}

object Index extends IndexFunctions

trait IndexFunctions {

  def index[S, I, A](i: I)(implicit ev: Index[S, I, A]): Optional[S, A] = ev.index(i)

  def atIndex[S, I, A](implicit ev: At[S, I, A]) = new Index[S, I, A] {
    def index(i: I) = ev.at(i) composePrism monocle.std.maybe.just
  }

}


