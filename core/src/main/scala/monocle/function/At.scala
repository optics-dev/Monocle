package monocle.function

import monocle.SimpleLens

trait At[S, I, A] {

  /**
   * Creates a Lens from S to an optional A at index i
   * at is strictly more powerful than index because we can add a value at an empty index
   */
  def at(i: I): SimpleLens[S, Option[A]]

}


object At extends AtFunctions

trait AtFunctions {

  def at[S, I, A](i: I)(implicit ev: At[S, I, A]): SimpleLens[S, Option[A]] = ev.at(i)

}
