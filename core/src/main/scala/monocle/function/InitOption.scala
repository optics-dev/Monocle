package monocle.function

import monocle.SimpleOptional
import scala.annotation.implicitNotFound

@deprecated("use Snoc", since = "0.6")
@implicitNotFound("Could not find an instance of InitOption[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait InitOption[S, A] {

  /**
   * Creates an Optional between S and its optional init A.
   * Init represents all the the elements of S except the last one
   */
  def initOption: SimpleOptional[S, A]

}

object InitOption extends InitOptionFunctions

trait InitOptionFunctions {

  def initOption[S, A](implicit ev: InitOption[S, A]): SimpleOptional[S, A] = ev.initOption

  def reverseTailInitOption[S](implicit evReverse: Reverse[S, S], evTail: TailOption[S, S]): InitOption[S, S] = new InitOption[S, S] {
    def initOption = evReverse.reverse composeOptional evTail.tailOption composeIso evReverse.reverse
  }

  def snocInitOption[S, A](implicit ev: Snoc[S, A]): InitOption[S, S] = new InitOption[S, S]{
    def initOption = ev.initMaybe
  }

}
