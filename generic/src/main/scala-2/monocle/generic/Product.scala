package monocle.generic

import monocle.PTraversal
import monocle.function.Each
import monocle.{Iso, Traversal}
import monocle.generic.internal.TupleGeneric
import cats.Applicative
import cats.syntax.apply._
import shapeless.{::, Generic, HList, HNil}

@deprecated("no replacement", since = "3.0.0-M1")
object product extends ProductOptics

trait ProductOptics {
  @deprecated("no replacement", since = "3.0.0-M1")
  def productToTuple[S <: Product](implicit ev: TupleGeneric[S]): Iso[S, ev.Repr] =
    Iso[S, ev.Repr](s => ev.to(s))(t => ev.from(t))

  @deprecated("no replacement", since = "3.0.0-M1")
  implicit def hNilEach[A] =
    new Each[HNil, A] {
      def each: Traversal[HNil, A] = Traversal.void[HNil, A]
    }

  @deprecated("no replacement", since = "3.0.0-M1")
  implicit def hConsEach[A, Rest <: HList](implicit restEach: Each[Rest, A]) =
    new Each[A :: Rest, A] {
      def each: Traversal[A :: Rest, A] =
        new PTraversal[A :: Rest, A :: Rest, A, A] {
          def modifyA[F[_]: Applicative](f: A => F[A])(s: A :: Rest): F[A :: Rest] =
            (f(s.head), restEach.each.modifyA(f)(s.tail)).mapN(_ :: _)
        }
    }

  @deprecated("no replacement", since = "3.0.0-M1")
  implicit def productEach[S, SGen <: HList, A](implicit
    gen: Generic.Aux[S, SGen],
    genEach: Each[SGen, A]
  ): Each[S, A] =
    new Each[S, A] {
      def each: Traversal[S, A] =
        new Traversal[S, A] {
          def modifyA[F[_]: Applicative](f: A => F[A])(s: S): F[S] =
            Applicative[F].map(genEach.each.modifyA(f)(gen.to(s)))(gen.from)
        }
    }
}
