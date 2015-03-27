package monocle.function

import monocle.Lens
import scala.annotation.implicitNotFound

@implicitNotFound("Could not find an instance of At[${S},${I},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait At[S, I, A] {

  /**
   * Creates a Lens from S to an optional A at index i
   * at is strictly more powerful than index because we can add a value at an empty index
   */
  def at(i: I): Lens[S, Option[A]]

}


object At extends AtFunctions

trait AtFunctions {

  def at[S, I, A](i: I)(implicit ev: At[S, I, A]): Lens[S, Option[A]] = ev.at(i)

}
