package monocle.std

import monocle.function._
import monocle.{Optional, Prism}

import scalaz.Id.Id
import scalaz.std.list._
import scalaz.std.option._
import scalaz.syntax.traverse._
import scalaz.{Applicative, \/}

object list extends ListOptics

trait ListOptics {

  implicit def listEmpty[A]: Empty[List[A]] = new Empty[List[A]] {
    def empty = Prism[List[A], Unit](l => if(l.isEmpty) Some(()) else None)(_ => List.empty)
  }

  implicit val nilEmpty: Empty[Nil.type] = new Empty[Nil.type] {
    def empty = Prism[Nil.type, Unit](_ => Some(()))(_ => Nil)
  }

  implicit def listReverse[A]: Reverse[List[A], List[A]] =
    reverseFromReverseFunction[List[A]](_.reverse)

  implicit def listEach[A]: Each[List[A], A] = Each.traverseEach[List, A]

  implicit def listIndex[A]: Index[List[A], Int, A] = new Index[List[A], Int, A] {
    def index(i: Int) = Optional[List[A], A](
      l      => if(i < 0) None else l.drop(i).headOption)(
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
      case Nil     => None
      case x :: xs => Some((x, xs))
    }{ case (a, s) => a :: s }
  }

  implicit def listSnoc[A]: Snoc[List[A], A] = new Snoc[List[A], A]{
    def snoc = Prism[List[A], (List[A], A)](
      s => Applicative[Option].apply2(\/.fromTryCatchNonFatal(s.init).toOption, s.lastOption)((_,_))){
      case (init, last) => init :+ last
    }
  }

}
