package monocle.std

import monocle.function._
import monocle.{Lens, SimpleIso, SimpleOptional, SimpleTraversal}

import scalaz.{Applicative, Maybe, OneAnd}

object oneand extends OneAndInstances

trait OneAndInstances {

  implicit def oneAndEach[T[_], A](implicit ev: Each[T[A], A]): Each[OneAnd[T, A], A] =
    new Each[OneAnd[T, A], A]{
      def each = new SimpleTraversal[OneAnd[T, A], A]{
        def _traversal[F[_]: Applicative](f: A => F[A])(s: OneAnd[T, A]): F[OneAnd[T, A]] =
          Applicative[F].apply2(f(s.head), ev.each.modifyF(f)(s.tail))((head, tail) => new OneAnd(head, tail))
      }
    }

  implicit def oneAndIndex[T[_], A](implicit ev: Index[T[A], Int, A]): Index[OneAnd[T, A], Int, A] =
    new Index[OneAnd[T, A], Int, A]{
      def index(i: Int) = i match {
        case 0 => SimpleOptional[OneAnd[T, A], A](oneAnd => Maybe.just(oneAnd.head))((a, oneAnd) => oneAnd.copy(head = a))
        case _ => SimpleOptional[OneAnd[T, A], A](oneAnd => ev.index(i - 1).getMaybe(oneAnd.tail))(
          (a, oneAnd) => oneAnd.copy(tail = ev.index(i - 1).set(a)(oneAnd.tail))
        )
      }
    }

  implicit def oneAndField1[T[_], A]: Field1[OneAnd[T, A], A] = new Field1[OneAnd[T, A], A]{
    def first = Lens[OneAnd[T, A], A](_.head)( (a, oneAnd) => oneAnd.copy(head = a))
  }

  implicit def oneAndCons1[T[_], A]: Cons1[OneAnd[T, A], A, T[A]] = new Cons1[OneAnd[T, A], A, T[A]] {
    def cons1 = SimpleIso[OneAnd[T, A], (A, T[A])](o => (o.head, o.tail)){ case (h, t) => OneAnd(h, t)}
  }

}
