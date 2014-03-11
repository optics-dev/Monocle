package monocle.thirdparty

import monocle.{SimpleLens, Iso, Lens}
import shapeless.HList._
import shapeless._
import shapeless.ops.hlist.{ReplaceAt, At}


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


  def first[S, A, L <: HList](implicit gen: Generic.Aux[S, L],
                                        at: At.Aux[L, shapeless.nat._0.N, A],
                                   replace: ReplaceAt.Aux[L, shapeless.nat._0.N, A, (A, L)]) = _at(shapeless.nat._0)



  def _at[S, A, L <: HList](n : Nat)(implicit gen: Generic.Aux[S, L],
                                               at: At.Aux[L, n.N, A],
                                          replace: ReplaceAt.Aux[L, n.N, A, (A, L)]) =
    SimpleLens[S, A](s => gen.to(s).at(n), (s, a) => gen.from(gen.to(s).updatedAt(n, a)) )

}
