package monocle.std

import scalaz.{ \/-, -\/, \/ }
import monocle.PPrism

object disjunction extends DisjunctionFunctions

trait DisjunctionFunctions {
  
  def left[A, B, C]: PPrism[A \/ B, C \/ B, A, C] =
    PPrism[A \/ B, C \/ B, A, C](_.swap.bimap(\/-.apply, identity))(-\/.apply)

  def right[A, B, C]: PPrism[A \/ B, A \/ C, B, C] =
    PPrism[A \/ B, A \/ C, B, C](_.bimap(-\/.apply, identity))(\/-.apply)
}
