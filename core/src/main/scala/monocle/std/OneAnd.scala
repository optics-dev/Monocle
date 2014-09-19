package monocle.std

import monocle.function._
import monocle.{SimpleIso, SimpleLens, SimpleOptional, SimpleTraversal}

import scalaz.{Applicative, Kleisli, Maybe, OneAnd}

object oneand extends OneAndInstances

trait OneAndInstances {

  implicit def oneAndEach[T[_], A](implicit ev: Each[T[A], A]): Each[OneAnd[T, A], A] =
    new Each[OneAnd[T, A], A]{
      def each = new SimpleTraversal[OneAnd[T, A], A]{
        def _traversal[F[_] : Applicative](f: Kleisli[F, A, A]) = Kleisli[F, OneAnd[T, A], OneAnd[T, A]]( s =>
          Applicative[F].apply2(f(s.head), ev.each.modifyK(f).apply(s.tail))((head, tail) => new OneAnd(head, tail))
        )
      }
    }

  implicit def oneAndIndex[T[_], A](implicit ev: Index[T[A], Int, A]): Index[OneAnd[T, A], Int, A] =
    new Index[OneAnd[T, A], Int, A]{
      def index(i: Int) = i match {
        case 0 => SimpleOptional[OneAnd[T, A], A](oneAnd => Maybe.just(oneAnd.head), (oneAnd, a) => oneAnd.copy(head = a))
        case _ => SimpleOptional[OneAnd[T, A], A](oneAnd => ev.index(i - 1).getMaybe(oneAnd.tail),
          (oneAnd, a) =>  oneAnd.copy(tail = ev.index(i - 1).set(a)(oneAnd.tail)) )
      }
    }

  implicit def oneAndField1[T[_], A]: Field1[OneAnd[T, A], A] = new Field1[OneAnd[T, A], A]{
    def first = SimpleLens[OneAnd[T, A], A](_.head, (oneAnd, a) => oneAnd.copy(head = a))
  }

  implicit def oneAndHCons[T[_], A]: HCons[OneAnd[T, A], A, T[A]] = new HCons[OneAnd[T, A], A, T[A]]{
    def hcons = SimpleIso[OneAnd[T, A], (A, T[A])](o => (o.head, o.tail), { case (h, t) => OneAnd(h, t) })
  }


}
