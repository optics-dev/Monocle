package monocle.function

import monocle.SimpleTraversal
import monocle.syntax.traversal._

trait Init[S] {

  def init: SimpleTraversal[S, S]

}

object Init extends InitInstances

trait InitInstances {

  def init[S](implicit ev: Init[S]): SimpleTraversal[S, S] = ev.init

  def reverseTail[S](implicit evReverse: Reverse[S], evTail: Tail[S]): Init[S] = new Init[S] {
    def init: SimpleTraversal[S, S] = evReverse.reverse |->> evTail.tail |->> evReverse.reverse
  }

  implicit def listInit[A]: Init[List[A]]     = reverseTail
  implicit def StreamInit[A]: Init[Stream[A]] = reverseTail
  implicit val stringInit: Init[String]       = reverseTail


}
