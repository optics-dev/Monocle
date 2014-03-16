package monocle.thirdparty

import monocle.{SimpleLens, Lens}
import shapeless.HList._
import shapeless.ops.hlist.{ReplaceAt, At}
import shapeless.{Nat, Generic, HList}


object generic extends GenericInstances

trait GenericInstances {

  /** Create a lens toward the first element of an S */
  def _1[S, A, L <: HList](implicit gen: Generic.Aux[S, L],
                              at: At.Aux[L, shapeless.nat._0.N, A],
                              replace: ReplaceAt.Aux[L, shapeless.nat._0.N, A, (A, L)]): Lens[S, S, A, A] =
    _at(shapeless.nat._0)

  /** Create a lens toward the second element of an S */
  def _2[S, A, L <: HList](implicit gen: Generic.Aux[S, L],
                               at: At.Aux[L, shapeless.nat._1.N, A],
                               replace: ReplaceAt.Aux[L, shapeless.nat._1.N, A, (A, L)]): Lens[S, S, A, A] =
    _at(shapeless.nat._1)

  /** Create a lens toward the third element of an S */
  def _3[S, A, L <: HList](implicit gen: Generic.Aux[S, L],
                              at: At.Aux[L, shapeless.nat._2.N, A],
                              replace: ReplaceAt.Aux[L, shapeless.nat._2.N, A, (A, L)]): Lens[S, S, A, A] =
    _at(shapeless.nat._2)



  private def _at[S, A, L <: HList](n : Nat)(implicit gen: Generic.Aux[S, L],
                                                       at: At.Aux[L, n.N, A],
                                                  replace: ReplaceAt.Aux[L, n.N, A, (A, L)]): Lens[S, S, A, A]  =
    SimpleLens[S, A](s => gen.to(s).at(n), (s, a) => gen.from(gen.to(s).updatedAt(n, a)) )

}
