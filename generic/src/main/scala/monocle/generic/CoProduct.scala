package monocle.generic

import monocle.{Iso, Prism}
import monocle.generic.internal.{CoproductToDisjunction, DisjunctionToCoproduct}
import shapeless.{Coproduct, Generic}
import shapeless.ops.coproduct.{CoproductToEither, EitherToCoproduct, Inject, Selector}

object coproduct extends CoProductInstances

trait CoProductInstances {
  def coProductPrism[C <: Coproduct, A](implicit evInject: Inject[C, A], evSelector: Selector[C, A]): Prism[C, A] =
    Prism[C, A](evSelector.apply(_))(evInject.apply)

  /** An isomorphism between a coproduct `S` and the sum of its parts.
    *
    * {{{
    *   type ISB = Int :+: String :+: Boolean :+: CNil
    *
    *   val iso: Iso[ISB, Either[Int, Either[String, Boolean]]] = coProductEitherIso[ISB].apply
    * }}}
    */
  def coProductEitherIso[S <: Coproduct]: GenCoProductEitherIso[S] = new GenCoProductEitherIso

  class GenCoProductEitherIso[S <: Coproduct] {
    def apply[L, R](
      implicit
      coproductToEither: CoproductToEither.Aux[S, Either[L, R]],
      eitherToCoproduct: EitherToCoproduct.Aux[L, R, S]
    ): Iso[S, Either[L, R]] =
      Iso(coproductToEither.apply)(eitherToCoproduct.apply)
  }

  /** An isomorphism between a sum type `S` (e.g. a sealed trait) and the sum of its parts.
    *
    * {{{
    *   sealed trait S
    *   case class A(name: String) extends S
    *   case class B(name: String) extends S
    *   case class C(otherName: String) extends S
    *
    *   val iso: Iso[S, Either[A, Either[B, C]]] = coProductToEither[S].apply
    * }}}
    */
  def coProductToEither[S]: GenCoProductToEither[S] = new GenCoProductToEither

  class GenCoProductToEither[S] {
    def apply[C <: Coproduct, L, R](
      implicit
      ev: Generic.Aux[S, C],
      coproductToEither: CoproductToEither.Aux[C, Either[L, R]],
      eitherToCoproduct: EitherToCoproduct.Aux[L, R, C]
    ): Iso[S, Either[L, R]] =
      generic.toGeneric[S] composeIso coProductEitherIso.apply
  }

  /** An isomorphism between a coproduct `S` and the sum of its parts.
    *
    * {{{
    *   type ISB = Int :+: String :+: Boolean :+: CNil
    *
    *   val iso: Iso[ISB, Either[Int, Either[String, Boolean]] = coProductDisjunctionIso[ISB].apply
    * }}}
    */
  def coProductDisjunctionIso[S <: Coproduct]: GenCoProductDisjunctionIso[S] = new GenCoProductDisjunctionIso

  class GenCoProductDisjunctionIso[S <: Coproduct] {
    def apply[L, R](
      implicit
      coproductToDisjunction: CoproductToDisjunction.Aux[S, Either[L, R]],
      disjunctionToCoproduct: DisjunctionToCoproduct.Aux[L, R, S]
    ): Iso[S, Either[L, R]] =
      Iso(coproductToDisjunction.apply)(disjunctionToCoproduct.apply)
  }

  /** An isomorphism between a sum type `S` (e.g. a sealed trait) and the sum of its parts.
    *
    * {{{
    *   sealed trait S
    *   case class A(name: String) extends S
    *   case class B(name: String) extends S
    *   case class C(otherName: String) extends S
    *
    *   val iso: Iso[S, Either[A, Either[B, C])] = coProductToDisjunction[S].apply
    * }}}
    */
  def coProductToDisjunction[S]: GenCoProductToDisjunction[S] = new GenCoProductToDisjunction

  class GenCoProductToDisjunction[S] {
    def apply[C <: Coproduct, L, R](
      implicit
      ev: Generic.Aux[S, C],
      coproductToDisjunction: CoproductToDisjunction.Aux[C, Either[L, R]],
      disjunctionToCoproduct: DisjunctionToCoproduct.Aux[L, R, C]
    ): Iso[S, Either[L, R]] =
      generic.toGeneric[S] composeIso coProductDisjunctionIso.apply
  }
}
