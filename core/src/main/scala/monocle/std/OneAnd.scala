package monocle.std

import monocle.function._
import monocle.{Traversal, SimpleTraversal, SimpleLens, SimpleOptional}

import scalaz.Maybe.{Empty, Just}
import scalaz.{Kleisli, Maybe, Applicative, OneAnd}

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

  implicit def oneAndHead[T[_], A]: Head[OneAnd[T, A], A] =
    Head.field1Head[OneAnd[T, A], A]

  implicit def oneAndTail[T[_], A]: Tail[OneAnd[T, A], T[A]] = new Tail[OneAnd[T, A], T[A]]{
    def tail = SimpleLens[OneAnd[T, A], T[A]](_.tail, (oneAnd, tail) => oneAnd.copy(tail = tail))
  }

  implicit def oneAndLastFromLastOption[T[_], A](implicit ev: LastOption[T[A], A]): Last[OneAnd[T, A], A] = new Last[OneAnd[T, A], A] {
    def last = SimpleLens[OneAnd[T, A], A](oneAnd => ev.lastOption.getMaybe(oneAnd.tail).getOrElse(oneAnd.head),
      (oneAnd, a) => ev.lastOption.setMaybe(a)(oneAnd.tail) match {
        case Just(newTail) => oneAnd.copy(tail = newTail)
        case Empty()       => oneAnd.copy(head = a)
      })
  }

  implicit def oneAndLastFromLast[T[_], A](implicit ev: Last[T[A], A]): Last[OneAnd[T, A], A] = new Last[OneAnd[T, A], A] {
    def last = SimpleLens[OneAnd[T, A], A](oneAnd => ev.last.get(oneAnd.tail),
      (oneAnd, a) => oneAnd.copy(tail = ev.last.set(a)(oneAnd.tail)))
  }


}
