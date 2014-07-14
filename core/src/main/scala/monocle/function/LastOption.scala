package monocle.function

import monocle.SimpleOptional

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

}

