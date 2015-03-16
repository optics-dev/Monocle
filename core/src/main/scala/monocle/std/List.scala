package monocle.std

import monocle.function._
import monocle.{Iso, Optional, PIso, Prism}

import scalaz.Id.Id
import scalaz.std.list._
import scalaz.syntax.std.option._
import scalaz.syntax.traverse._
import scalaz.{Applicative, IList, Maybe}

object list extends ListInstances

trait ListInstances {

  /** [[PIso]] between a [[scala.List]] and an [[scalaz.IList]] */
  def pListToIList[A, B]: PIso[List[A], List[B], IList[A], IList[B]] =
    pIListToList[B, A].reverse

  /** monormorphic alias for pListToIList */
  def listToIList[A]: Iso[List[A], IList[A]] =
    pListToIList[A, A]

  implicit def listEmpty[A]: Empty[List[A]] = new Empty[List[A]] {
    def empty = Prism[List[A], Unit](l => if(l.isEmpty) Maybe.just(()) else Maybe.empty)(_ => List.empty)
  }

  implicit val nilEmpty: Empty[Nil.type] = new Empty[Nil.type] {
    def empty = Prism[Nil.type, Unit](_ => Maybe.just(()))(_ => Nil)
  }

  implicit def listReverse[A]: Reverse[List[A], List[A]] =
    reverseFromReverseFunction[List[A]](_.reverse)

  implicit def listEach[A]: Each[List[A], A] = Each.traverseEach[List, A]

  implicit def listIndex[A]: Index[List[A], Int, A] = new Index[List[A], Int, A] {
    def index(i: Int) = Optional[List[A], A](
      l      => if(i < 0) Maybe.empty else l.drop(i).headOption.toMaybe)(
      a => l => l.zipWithIndex.traverse[Id, A]{
        case (_    , index) if index == i => a
        case (value, index)               => value
      }
    )
  }

  implicit def listFilterIndex[A]: FilterIndex[List[A], Int, A] =
    FilterIndex.traverseFilterIndex[List, A](_.zipWithIndex)

  implicit def listCons[A]: Cons[List[A], A] = new Cons[List[A], A]{
    def cons = Prism[List[A], (A, List[A])]{
      case Nil     => Maybe.empty
      case x :: xs => Maybe.just((x, xs))
    }{ case (a, s) => a :: s }
  }

  implicit def listSnoc[A]: Snoc[List[A], A] = new Snoc[List[A], A]{
    def snoc = Prism[List[A], (List[A], A)](
      s => Applicative[Maybe].apply2(Maybe.fromTryCatchNonFatal(s.init), s.lastOption.toMaybe)((_,_))){
      case (init, last) => init :+ last
    }
  }

}
