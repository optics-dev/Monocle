package monocle.generic

import monocle.function._
import monocle.{Iso, Lens}
import shapeless.ops.hlist.{At, Init => HInit, IsHCons, Last => HLast, Prepend, ReplaceAt, Reverse => HReverse}
import shapeless.{Lens => _, _}

@deprecated("no replacement", since = "3.0.0-M1")
object hlist extends HListInstances

trait HListInstances {
  @deprecated("no replacement", since = "3.0.0-M1")
  def hListAt[S <: HList, A](
    n: Nat
  )(implicit evAt: At.Aux[S, n.N, A], evReplace: ReplaceAt.Aux[S, n.N, A, (A, S)]): Lens[S, A] =
    Lens[S, A](_.at(n))(a => hlist => hlist.updatedAt(n, a))

  @deprecated("no replacement", since = "3.0.0-M1")
  def toHList[S, A <: HList](implicit gen: Generic.Aux[S, A]): Iso[S, A] =
    Iso[S, A](s => gen.to(s))(l => gen.from(l))

  @deprecated("no replacement", since = "3.0.0-M1")
  def fromHList[S <: HList, A](implicit gen: Generic.Aux[A, S]): Iso[S, A] =
    toHList.reverse

  implicit def hListReverse[S <: HList, A <: HList](implicit
    ev1: HReverse.Aux[S, A],
    ev2: HReverse.Aux[A, S]
  ): Reverse[S, A] =
    new Reverse[S, A] {
      def reverse = Iso[S, A](ev1.apply)(ev2.apply)
    }

  @deprecated("no replacement", since = "3.0.0-M1")
  implicit def hListCons1[S <: HList, H, T <: HList](implicit
    evIsCons: IsHCons.Aux[S, H, T],
    evPrepend: Prepend.Aux[H :: HNil, T, S]
  ): Cons1[S, H, T] =
    new Cons1[S, H, T] {
      def cons1 = Iso[S, (H, T)](s => (evIsCons.head(s), evIsCons.tail(s))) { case (h, t) => evPrepend(h :: HNil, t) }
    }

  @deprecated("no replacement", since = "3.0.0-M1")
  implicit def hListSnoc1[S <: HList, I <: HList, L](implicit
    evInit: HInit.Aux[S, I],
    evLast: HLast.Aux[S, L],
    evPrepend: Prepend.Aux[I, L :: HNil, S]
  ): Snoc1[S, I, L] =
    new Snoc1[S, I, L] {
      def snoc1 = Iso[S, (I, L)](s => (evInit(s), evLast(s))) { case (i, l) => evPrepend(i, l :: HNil) }
    }

  @deprecated("no replacement", since = "3.0.0-M1")
  implicit def hListField1[S <: HList, A](implicit
    evAt: At.Aux[S, shapeless.nat._0.N, A],
    evReplace: ReplaceAt.Aux[S, shapeless.nat._0.N, A, (A, S)]
  ): Field1[S, A] =
    new Field1[S, A] {
      def first = hListAt(shapeless.nat._0)
    }

  @deprecated("no replacement", since = "3.0.0-M1")
  implicit def hListField2[S <: HList, A](implicit
    evAt: At.Aux[S, shapeless.nat._1.N, A],
    evReplace: ReplaceAt.Aux[S, shapeless.nat._1.N, A, (A, S)]
  ): Field2[S, A] =
    new Field2[S, A] {
      def second = hListAt(shapeless.nat._1)
    }

  @deprecated("no replacement", since = "3.0.0-M1")
  implicit def hListField3[S <: HList, A](implicit
    evAt: At.Aux[S, shapeless.nat._2.N, A],
    evReplace: ReplaceAt.Aux[S, shapeless.nat._2.N, A, (A, S)]
  ): Field3[S, A] =
    new Field3[S, A] {
      def third = hListAt(shapeless.nat._2)
    }

  @deprecated("no replacement", since = "3.0.0-M1")
  implicit def hListField4[S <: HList, A](implicit
    evAt: At.Aux[S, shapeless.nat._3.N, A],
    evReplace: ReplaceAt.Aux[S, shapeless.nat._3.N, A, (A, S)]
  ): Field4[S, A] =
    new Field4[S, A] {
      def fourth = hListAt(shapeless.nat._3)
    }

  @deprecated("no replacement", since = "3.0.0-M1")
  implicit def hListField5[S <: HList, A](implicit
    evAt: At.Aux[S, shapeless.nat._4.N, A],
    evReplace: ReplaceAt.Aux[S, shapeless.nat._4.N, A, (A, S)]
  ): Field5[S, A] =
    new Field5[S, A] {
      def fifth = hListAt(shapeless.nat._4)
    }

  @deprecated("no replacement", since = "3.0.0-M1")
  implicit def hListField6[S <: HList, A](implicit
    evAt: At.Aux[S, shapeless.nat._5.N, A],
    evReplace: ReplaceAt.Aux[S, shapeless.nat._5.N, A, (A, S)]
  ): Field6[S, A] =
    new Field6[S, A] {
      def sixth = hListAt(shapeless.nat._5)
    }
}
