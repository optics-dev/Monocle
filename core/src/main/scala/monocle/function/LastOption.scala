package monocle.function

import monocle.SimpleOptional
import scala.annotation.implicitNotFound

@deprecated("use Snoc", since = "0.6")
@implicitNotFound("Could not find an instance of LastOption[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait LastOption[S, A] {

  /** Creates a Traversal from S to its optional last element */
  def lastOption: SimpleOptional[S, A]

}


object LastOption extends LastOptionFunctions

trait LastOptionFunctions {

  def lastOption[S, A](implicit ev: LastOption[S, A]): SimpleOptional[S, A] = ev.lastOption

  def reverseHeadLastOption[S, A](implicit evReverse: Reverse[S, S], evHead: HeadOption[S, A]) = new LastOption[S, A] {
    def lastOption = evReverse.reverse composeOptional evHead.headOption
  }

  def snocLastOption[S, A](implicit ev: Snoc[S, A]): LastOption[S, A] = new LastOption[S, A]{
    def lastOption = ev.lastMaybe
  }

}

