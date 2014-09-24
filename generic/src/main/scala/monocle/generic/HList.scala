package monocle.generic

import monocle._
import monocle.function._
import shapeless._
import shapeless.ops.hlist.{Reverse => HReverse, Prepend, IsHCons, ReplaceAt, At, Init => HInit, Last => HLast}


object hlist extends HListInstances

trait HListInstances {

  def toHList[S, A <: HList](implicit gen: Generic.Aux[S, A]): SimpleIso[S, A] =
    SimpleIso[S, A]({ s => gen.to(s)}, {l => gen.from(l)} )

  def fromHList[S <: HList, A](implicit gen: Generic.Aux[A, S]): SimpleIso[S, A] =
    toHList.reverse

  implicit def hListReverse[S <: HList, A <: HList](implicit ev1: HReverse.Aux[S, A],
                                                    ev2: HReverse.Aux[A, S]): Reverse[S, A] = new Reverse[S, A]{
    def reverse = SimpleIso[S, A](ev1.apply, ev2.apply)
  }

  implicit def hListHead[S <: HList, A](implicit evFirst: Field1[S, A]) = new Head[S, A] {
    def head = evFirst.first
  }

  implicit def hListLast[S <: HList, RS <: HList, A](implicit evReverse: Reverse[S, RS],
                                                                 evHead: Head[RS, A]) = new Last[S, A] {
    def last = evReverse.reverse composeLens evHead.head
  }

  implicit def hListTail[S <: HList, H, T <: HList](implicit evIsCons: IsHCons.Aux[S, H, T],
                                                            evPrepend: Prepend.Aux[H :: HNil, T, S]) = new Tail[S, T] {
    def tail = SimpleLens[S, T](s => evIsCons.tail(s), (a, s) => evPrepend(evIsCons.head(s) :: HNil, a))
  }

  implicit def hListInit[S <: HList, L, A <: HList](implicit evInit: HInit.Aux[S, A],
                                                             evLast: HLast.Aux[S, L],
                                                    evPrepend: Prepend.Aux[A, L :: HNil, S]) = new Init[S, A] {
    def init = SimpleLens[S, A](s => evInit(s), (a, s) => evPrepend(a, evLast(s) :: HNil))
  }

  implicit def hListField1[S <: HList, A](implicit evAt: At.Aux[S, shapeless.nat._0.N, A],
                                              evReplace: ReplaceAt.Aux[S, shapeless.nat._0.N, A, (A, S)]) = new Field1[S, A] {
    def first = hListAt(shapeless.nat._0)
  }

  implicit def hListField2[S <: HList, A](implicit evAt: At.Aux[S, shapeless.nat._1.N, A],
                                              evReplace: ReplaceAt.Aux[S, shapeless.nat._1.N, A, (A, S)]) = new Field2[S, A] {
    def second = hListAt(shapeless.nat._1)
  }

  implicit def hListField3[S <: HList, A](implicit evAt: At.Aux[S, shapeless.nat._2.N, A],
                                              evReplace: ReplaceAt.Aux[S, shapeless.nat._2.N, A, (A, S)]) = new Field3[S, A] {
    def third = hListAt(shapeless.nat._2)
  }

  implicit def hListField4[S <: HList, A](implicit evAt: At.Aux[S, shapeless.nat._3.N, A],
                                              evReplace: ReplaceAt.Aux[S, shapeless.nat._3.N, A, (A, S)]) = new Field4[S, A] {
    def fourth = hListAt(shapeless.nat._3)
  }

  implicit def hListField5[S <: HList, A](implicit evAt: At.Aux[S, shapeless.nat._4.N, A],
                                              evReplace: ReplaceAt.Aux[S, shapeless.nat._4.N, A, (A, S)]) = new Field5[S, A] {
    def fifth = hListAt(shapeless.nat._4)
  }

  implicit def hListField6[S <: HList, A](implicit evAt: At.Aux[S, shapeless.nat._5.N, A],
                                              evReplace: ReplaceAt.Aux[S, shapeless.nat._5.N, A, (A, S)]) = new Field6[S, A] {
    def sixth = hListAt(shapeless.nat._5)
  }


  private def hListAt[S <: HList, A](n : Nat)(implicit evAt: At.Aux[S, n.N, A],
                                                  evReplace: ReplaceAt.Aux[S, n.N, A, (A, S)]): SimpleLens[S, A]  =
    SimpleLens[S, A](_.at(n), (a, hlist) => hlist.updatedAt(n, a) )

}
