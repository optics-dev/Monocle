package monocle.thirdparty

import monocle.{SimpleLens, Iso, Lens}
import shapeless._


object hlist extends HListInstances

trait HListInstances {

  type HL[+H, T <: HList] = shapeless.::[H, T]

  def _1[A, T <: HList, New] = Lens[HL[A, T], HL[New, T], A, New](_.head, (l, a) => l.copy(head = a))
  def _2[A1, A2, T <: HList, New] =
    Lens[HL[A1, HL[A2, T]], HL[A1, HL[New, T]], A2, New](_.tail.head, (l, a) => l.copy(tail = l.tail.copy(head = a)))


  def pairToHListIso[A1, A2, B1, B2] =
    Iso[(A1, A2), (B1, B2), HL[A1, HL[A2, HNil]], HL[B1, HL[B2, HNil]]](t => t._1 :: t._2 :: HNil,
      hl => hl.head -> hl.tail.head
    )

  def first[S, A, L <: HList](implicit gen : Generic.Aux[S, L], nth : HListNthLensAux[L, shapeless.nat._0.N, A]) = at(shapeless.nat._0)

  def at[S, A, L <: HList](n : Nat)(implicit gen : Generic.Aux[S, L], nth : HListNthLensAux[L, n.N, A]) =
    SimpleLens[S, A](s => nth.get(gen.to(s)), (s, a) => gen.from(nth.set(gen.to(s))(a)) )

}
