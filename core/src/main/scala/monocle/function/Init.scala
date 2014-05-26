package monocle.function

import monocle.SimpleOptional
import scalaz.IList

trait Init[S, A] {

  /**
   * Creates an Optional between S and its optional init A
   * init represents all the the elements of S except the last one
   */
  def init: SimpleOptional[S, A]

}

object Init extends InitInstances

trait InitInstances {

  def init[S, A](implicit ev: Init[S, A]): SimpleOptional[S, A] = ev.init

  def reverseTail[S](implicit evReverse: Reverse[S, S], evTail: TailOption[S, S]): Init[S, S] = new Init[S, S] {
    def init = evReverse.reverse composeOptional evTail.tailOption composeOptional evReverse.reverse
  }

  implicit def listInit[A]   = reverseTail[List[A]]
  implicit def StreamInit[A] = reverseTail[Stream[A]]
  implicit def vectorInit[A] = reverseTail[Vector[A]]
  implicit def iListInit[A]  = reverseTail[IList[A]]


  implicit val stringInit    = reverseTail[String]

}
