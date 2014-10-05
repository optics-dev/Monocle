package monocle.internal

import scalaz.{-\/, \/, \/-}

/**
 * (From Haskell) A Tagged s b value is a value b with an attached phantom type s.
 * This can be used in place of the more traditional but less safe idiom of passing in an undefined
 * value with the type, because unlike an (s -> b), a Tagged s b can't try to use the argument s as a real value.
 */
case class Tagged[S, B](untagged: B) {
  def retag[T]: Tagged[T, B] = Tagged(untagged)
}

object Tagged extends TaggedInstances

sealed abstract class TaggedInstances {
  implicit val taggedProChoice: ProChoice[Tagged] = new TaggedProChoice{}
}


private sealed trait TaggedProChoice extends ProChoice[Tagged]{
  override def dimap[A, B, C, D](pab: Tagged[A, B])(f: C => A)(g: B => D): Tagged[C, D] =
    Tagged(g(pab.untagged))
  def mapfst[A, B, C](fab: Tagged[A, B])(f: C => A): Tagged[C, B] = fab.retag[C]
  def mapsnd[A, B, C](fab: Tagged[A, B])(f: B => C): Tagged[A, C] = Tagged(f(fab.untagged))

  override def left[A, B, C](pab : Tagged[A, B]): Tagged[A \/ C, B \/ C] =
    Tagged(-\/(pab.untagged))
  override def right[A, B, C](pab: Tagged[A, B]): Tagged[C \/ A, C \/ B] =
    Tagged(\/-(pab.untagged))
}
