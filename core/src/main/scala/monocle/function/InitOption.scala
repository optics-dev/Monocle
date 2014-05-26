package monocle.function

import monocle.SimpleOptional
import scalaz.IList

trait InitOption[S, A] {

  /**
   * Creates an Optional between S and its optional init A.
   * Init represents all the the elements of S except the last one
   */
  def initOption: SimpleOptional[S, A]

}

object InitOption extends InitOptionInstances

trait InitOptionInstances {

  def initOption[S, A](implicit ev: InitOption[S, A]): SimpleOptional[S, A] = ev.initOption

  def reverseTail[S](implicit evReverse: Reverse[S, S], evTail: TailOption[S, S]): InitOption[S, S] = new InitOption[S, S] {
    def initOption = evReverse.reverse composeOptional evTail.tailOption composeOptional evReverse.reverse
  }

  implicit def listInit[A]   = reverseTail[List[A]]
  implicit def StreamInit[A] = reverseTail[Stream[A]]
  implicit def vectorInit[A] = reverseTail[Vector[A]]
  implicit def iListInit[A]  = reverseTail[IList[A]]


  implicit val stringInit    = reverseTail[String]

}
