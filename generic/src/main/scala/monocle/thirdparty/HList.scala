package monocle.thirdparty

import monocle.{SimpleIso, Iso, Lens}
import shapeless.{Generic, HList}

object hlist extends HListInstances

trait HListInstances {

  type HL[+H, T <: HList] = shapeless.::[H, T]

  /** Create a lens toward the first element of an HList */
  def _1[A, T <: HList, New] = Lens[HL[A, T], HL[New, T], A, New](_.head, (l, a) => l.copy(head = a))

  /** Create a lens toward the second element of an HList */
  def _2[A1, A2, T <: HList, New] =
    Lens[HL[A1, HL[A2, T]], HL[A1, HL[New, T]], A2, New](_.tail.head, (l, a) => l.copy(tail = l.tail.copy(head = a)))

  /** Create an iso between an S and its HList representation  */
  def toHList[S, L <: HList](implicit gen: Generic.Aux[S, L]): SimpleIso[S, L] =
    Iso[S, S, L, L]({ s => gen.to(s)}, {l => gen.from(l)} )



}
