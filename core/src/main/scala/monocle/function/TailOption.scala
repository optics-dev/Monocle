package monocle.function

import monocle.{Optional, SimpleOptional}
import scalaz.{INil, IList, ICons, Applicative}

trait TailOption[S, A] {

  /**
   * Creates an Optional between S and its optional tail A
   */
  def tailOption: SimpleOptional[S, A]

}

object TailOption extends TailOptionFunctions

trait TailOptionFunctions {

  def tailOption[S, A](implicit ev: TailOption[S, A]): SimpleOptional[S, A] = ev.tailOption

}
