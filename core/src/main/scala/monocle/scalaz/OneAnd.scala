package monocle.scalaz

import monocle.function._
import monocle.{SimpleTraversal, SimpleLens, SimpleOptional}

import _root_.scalaz.{Applicative, OneAnd}

object oneand extends OneAndInstances

trait OneAndInstances {

  implicit def oneAndEach[T[_], A](implicit ev: Each[T[A], A]): Each[OneAnd[T, A], A] =
    new Each[OneAnd[T, A], A]{
      def each = new SimpleTraversal[OneAnd[T, A], A]{
        def multiLift[F[_] : Applicative](from: OneAnd[T, A], f: A => F[A]): F[OneAnd[T, A]] =
          Applicative[F].apply2(f(from.head), ev.each.multiLift(from.tail, f))((head, tail) => new OneAnd(head, tail))
      }
    }

  implicit def oneAndIndex[T[_], A](implicit ev: Index[T[A], Int, A]): Index[OneAnd[T, A], Int, A] =
    new Index[OneAnd[T, A], Int, A]{
      def index(i: Int) = i match {
        case 0 => SimpleOptional[OneAnd[T, A], A](oneAnd => Some(oneAnd.head), (oneAnd, a) => oneAnd.copy(head = a))
        case _ => SimpleOptional[OneAnd[T, A], A](oneAnd => ev.index(i - 1).getOption(oneAnd.tail),
          (oneAnd, a) =>  oneAnd.copy(tail = ev.index(i - 1).set(oneAnd.tail, a)) )
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
    def last = SimpleLens[OneAnd[T, A], A](oneAnd => ev.lastOption.getOption(oneAnd.tail).getOrElse(oneAnd.head),
      (oneAnd, a) => ev.lastOption.setOption(oneAnd.tail, a) match {
        case Some(newTail) => oneAnd.copy(tail = newTail)
        case None          => oneAnd.copy(head = a)
      })
  }

  implicit def oneAndLastFromLast[T[_], A](implicit ev: Last[T[A], A]): Last[OneAnd[T, A], A] = new Last[OneAnd[T, A], A] {
    def last = SimpleLens[OneAnd[T, A], A](oneAnd => ev.last.get(oneAnd.tail),
      (oneAnd, a) => oneAnd.copy(tail = ev.last.set(oneAnd.tail, a)))
  }


}
