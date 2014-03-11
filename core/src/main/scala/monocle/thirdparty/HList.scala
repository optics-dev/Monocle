package monocle.thirdparty

import monocle.Lens
import shapeless.HList

object hlist extends HListInstances

trait HListInstances {

  type HL[+H, T <: HList] = shapeless.::[H, T]

  def _1[A, T <: HList, New] = Lens[HL[A, T], HL[New, T], A, New](_.head, (l, a) => l.copy(head = a))

  def _2[A1, A2, T <: HList, New] =
    Lens[HL[A1, HL[A2, T]], HL[A1, HL[New, T]], A2, New](_.tail.head, (l, a) => l.copy(tail = l.tail.copy(head = a)))

}
