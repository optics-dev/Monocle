package monocle.thirdparty

import monocle._
import monocle.function._
import shapeless.ops.hlist.{ReplaceAt, At}
import shapeless.{Generic, Nat, HList}


object hlist extends HListInstances

trait HListInstances {

  def toHList[S, A <: HList](implicit gen: Generic.Aux[S, A]): SimpleIso[S, A] =
    SimpleIso[S, A]({ s => gen.to(s)}, {l => gen.from(l)} )

  def fromHList[S <: HList, A](implicit gen: Generic.Aux[A, S]): SimpleIso[S, A] =
    toHList.reverse


  implicit def hListField1[S <: HList, A](implicit evAt: At.Aux[S, shapeless.nat._0.N, A],
                                              evReplace: ReplaceAt.Aux[S, shapeless.nat._0.N, A, (A, S)]) = new Field1[S, A] {
    def _1: SimpleLens[S, A] = hListAt(shapeless.nat._0)
  }

  implicit def hListField2[S <: HList, A](implicit evAt: At.Aux[S, shapeless.nat._1.N, A],
                                              evReplace: ReplaceAt.Aux[S, shapeless.nat._1.N, A, (A, S)]) = new Field2[S, A] {
    def _2: SimpleLens[S, A] = hListAt(shapeless.nat._1)
  }

  implicit def hListField3[S <: HList, A](implicit evAt: At.Aux[S, shapeless.nat._2.N, A],
                                              evReplace: ReplaceAt.Aux[S, shapeless.nat._2.N, A, (A, S)]) = new Field3[S, A] {
    def _3: SimpleLens[S, A] = hListAt(shapeless.nat._2)
  }

  implicit def hListField4[S <: HList, A](implicit evAt: At.Aux[S, shapeless.nat._3.N, A],
                                              evReplace: ReplaceAt.Aux[S, shapeless.nat._3.N, A, (A, S)]) = new Field4[S, A] {
    def _4: SimpleLens[S, A] = hListAt(shapeless.nat._3)
  }

  implicit def hListField5[S <: HList, A](implicit evAt: At.Aux[S, shapeless.nat._4.N, A],
                                              evReplace: ReplaceAt.Aux[S, shapeless.nat._4.N, A, (A, S)]) = new Field5[S, A] {
    def _5: SimpleLens[S, A] = hListAt(shapeless.nat._4)
  }

  implicit def hListField6[S <: HList, A](implicit evAt: At.Aux[S, shapeless.nat._5.N, A],
                                              evReplace: ReplaceAt.Aux[S, shapeless.nat._5.N, A, (A, S)]) = new Field6[S, A] {
    def _6: SimpleLens[S, A] = hListAt(shapeless.nat._5)
  }


  private def hListAt[S <: HList, A](n : Nat)(implicit evAt: At.Aux[S, n.N, A],
                                                  evReplace: ReplaceAt.Aux[S, n.N, A, (A, S)]): SimpleLens[S, A]  =
    SimpleLens[S, A](_.at(n), (hlist, a) => hlist.updatedAt(n, a) )

}
