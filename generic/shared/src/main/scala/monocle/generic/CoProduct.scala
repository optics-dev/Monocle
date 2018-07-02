package monocle.generic

import monocle.{Iso, Prism}
import monocle.generic.internal.{CoproductToDisjunction, DisjunctionToCoproduct}
import shapeless.Coproduct
import shapeless.ops.coproduct.{CoproductToEither, EitherToCoproduct, Inject, Selector}
import scalaz.{\/}

object coproduct extends CoProductInstances


trait CoProductInstances {
  
  def coProductPrism[C <: Coproduct, A](implicit evInject: Inject[C, A], evSelector: Selector[C, A]): Prism[C, A] =
    Prism[C, A](evSelector.apply(_))(evInject.apply)

  /** An isomorphism between a coproduct [[S]] and the sum of its parts.
    *
    * {{{
    *   type ISB = Int :+: String :+: Boolean :+: CNil
    *
    *   val iso: Iso[ISB, Either[Int, Either[String, Boolean]]] = coProductIso[ISB].apply
    * }}}
    */
  def coProductIso[S <: Coproduct]: GenCoProductIso[S] = new GenCoProductIso[S]

  class GenCoProductIso[S <: Coproduct] {
    def apply[L, R](implicit
                    coproductToEither: CoproductToEither.Aux[S, Either[L, R]],
                    eitherToCoproduct: EitherToCoproduct.Aux[L, R, S]
                   ): Iso[S, Either[L, R]] =
      Iso(coproductToEither.apply)(eitherToCoproduct.apply)
  }

  /** An isomorphism between a coproduct [[S]] and the sum of its parts.
    *
    * {{{
    *   type ISB = Int :+: String :+: Boolean :+: CNil
    *
    *   val iso: Iso[ISB, Int \/ (String \/ Boolean)] = coProductDisjunctionIso[ISB].apply
    * }}}
    */
  def coProductDisjunctionIso[S <: Coproduct]: GenCoProductDisjunctionIso[S] = new GenCoProductDisjunctionIso[S]

  class GenCoProductDisjunctionIso[S <: Coproduct] {
    def apply[L, R](implicit
                    coproductToDisjunction: CoproductToDisjunction.Aux[S, L \/ R],
                    disjunctionToCoproduct: DisjunctionToCoproduct.Aux[L, R, S]
                   ): Iso[S, L \/ R] =
      Iso(coproductToDisjunction.apply)(disjunctionToCoproduct.apply)
  }
}
