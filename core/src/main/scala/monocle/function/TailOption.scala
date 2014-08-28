package monocle.function

import monocle.{Optional, SimpleOptional}
import scalaz.{INil, IList, ICons, Applicative}
import scala.annotation.implicitNotFound

@deprecated("use Cons", since = "0.6")
@implicitNotFound("Could not find an instance of TailOption[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait TailOption[S, A] {

  /**
   * Creates an Optional between S and its optional tail A
   */
  def tailOption: SimpleOptional[S, A]

}

object TailOption extends TailOptionFunctions

trait TailOptionFunctions {

  def tailOption[S, A](implicit ev: TailOption[S, A]): SimpleOptional[S, A] = ev.tailOption

  def consTailOption[S, A](implicit ev: Cons[S, A]): TailOption[S, S] = new TailOption[S, S]{
    def tailOption = ev.tailMaybe
  }

}
