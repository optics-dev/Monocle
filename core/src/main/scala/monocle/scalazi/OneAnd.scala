package monocle.scalazi

import monocle.function._
import monocle.syntax._
import monocle.{SimpleOptional, SimpleLens}
import scalaz.{OneAnd, Traverse}

object oneand extends OneAndInstances

trait OneAndInstances {

  implicit def oneAndEach[T[_]: Traverse, A]: Each[OneAnd[T, A], A] =
    Each.traverseEach[({type λ[α] = OneAnd[T, α]})#λ, A]

  implicit def oneAndIndex[A, T[_]](implicit ev: Index[T[A], Int, A]): Index[OneAnd[T, A], Int, A] =
    new Index[OneAnd[T, A], Int, A]{
      def index(i: Int) =
        if(i == 0) SimpleOptional.build[OneAnd[T, A], A](oneAnd => Some(oneAnd ^|-> head get), (oneAnd, a) => oneAnd ^|-> head set a)
        else SimpleOptional.build[OneAnd[T, A], A](_.tail ^|-? ev.index(i - 1) getOption,
          (oneAnd, a) => oneAnd.copy(tail = oneAnd.tail ^|-? ev.index(i - 1) set a) )
    }

  implicit def oneAndField1[T[_], A]: Field1[OneAnd[T, A], A] = new Field1[OneAnd[T, A], A]{
    def first = SimpleLens[OneAnd[T, A], A](_.head, (oneAnd, a) => oneAnd.copy(head = a))
  }

  implicit def oneAndHead[T[_], A]: Head[OneAnd[T, A], A] =
    Head.field1Head[OneAnd[T, A], A]

  implicit def oneAndTail[T[_], A]: Tail[OneAnd[T, A], T[A]] = new Tail[OneAnd[T, A], T[A]]{
    def tail = SimpleLens[OneAnd[T, A], T[A]](_.tail, (oneAnd, tail) => oneAnd.copy(tail = tail))
  }

  implicit def oneAndLastOption[A, T[_]](implicit ev: LastOption[T[A], A]): LastOption[OneAnd[T, A], A] = new LastOption[OneAnd[T, A], A] {
    def lastOption = SimpleOptional.build[OneAnd[T, A], A](oneAnd => ev.lastOption.getOption(oneAnd.tail),
      (oneAnd, a) => oneAnd.copy(tail = ev.lastOption.set(oneAnd.tail, a)))
  }


}
