package monocle.function

import monocle.SimpleOptional
import scala.annotation.implicitNotFound

@deprecated("use Cons", since = "0.6")
@implicitNotFound("Could not find an instance of HeadOption[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait HeadOption[S, A] {

  /** Creates a Traversal from S to its optional first element */
  def headOption: SimpleOptional[S, A]

}


object HeadOption extends HeadOptionFunctions

trait HeadOptionFunctions {

  def headOption[S, A](implicit ev: HeadOption[S, A]): SimpleOptional[S, A] = ev.headOption

  def indexHeadOption[S, A](implicit ev: Index[S, Int, A]): HeadOption[S, A] = new HeadOption[S, A] {
    def headOption = index(0)
  }

  def consHeadOption[S, A](implicit ev: Cons[S, A]): HeadOption[S, A] = new HeadOption[S, A]{
    def headOption = ev.headMaybe
  }

}