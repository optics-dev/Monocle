package monocle.std

import monocle.function._
import monocle.{SimpleOptional, SimplePrism}

import scalaz.Id.Id
import scalaz.Maybe
import scalaz.std.list._
import scalaz.syntax.traverse._

object list extends ListInstances

trait ListInstances {

  implicit def listEmpty[A]: Empty[List[A]] = new Empty[List[A]] {
    def empty = SimplePrism[List[A], Unit](l => if(l.isEmpty) Maybe.just(()) else Maybe.empty, _ => List.empty)
  }

  implicit val nilEmpty: Empty[Nil.type] = new Empty[Nil.type] {
    def empty = SimplePrism[Nil.type, Unit](_ => Maybe.just(()), _ => Nil)
  }

  implicit def listReverse[A]: Reverse[List[A], List[A]] =
    reverseFromReverseFunction[List[A]](_.reverse)

  implicit def listEach[A]: Each[List[A], A] = Each.traverseEach[List, A]

  implicit def listIndex[A]: Index[List[A], Int, A] = new Index[List[A], Int, A] {
    def index(i: Int) = SimpleOptional[List[A], A](
      l      => if(i < 0) Maybe.empty else Maybe.optionMaybeIso.to(l.drop(i).headOption),
      (a, l) => l.zipWithIndex.traverse[Id, A]{
        case (_    , index) if index == i => a
        case (value, index)               => value
      }
    )
  }

  implicit def listFilterIndex[A]: FilterIndex[List[A], Int, A] =
    FilterIndex.traverseFilterIndex[List, A](_.zipWithIndex)

  implicit def listCons[A]: Cons[List[A], A] = new Cons[List[A], A]{
    def _cons = SimplePrism[List[A], (A, List[A])]({
      case Nil     => Maybe.empty
      case x :: xs => Maybe.just(x, xs)
    }, { case (a, s) => a :: s })
  }

  implicit def listSnoc[A]: Snoc[List[A], A] = new Snoc[List[A], A]{
    def snoc = SimplePrism[List[A], (List[A], A)]( s =>
      for {
        init <- if(s.isEmpty) Maybe.empty else Maybe.just(s.init)
        last <- if(s.isEmpty) Maybe.empty else Maybe.just(s.last)
      } yield (init, last),
    { case (init, last) => init :+ last }
    )
  }

}
