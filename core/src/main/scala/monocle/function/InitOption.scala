package monocle.function

import monocle.SimpleOptional

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
    def initOption = evReverse.reverse composeOptional evTail.tailOption composeOptional evReverse.reverse
  }

}
