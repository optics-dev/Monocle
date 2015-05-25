package monocle.std

import monocle.function._
import monocle.{PPrism, Prism}

import scalaz.{-\/, \/-}

object option extends OptionInstances

trait OptionFunctions {

  def some[A, B]: PPrism[Option[A], Option[B], A, B] =
    PPrism[Option[A], Option[B], A, B](_.map(\/-(_)) getOrElse -\/(None))(Some.apply)

  def none[A]: Prism[Option[A], Unit] =
    Prism[Option[A], Unit]{ case None => Some(()); case Some(_) => None }(_ => None)

}

trait OptionInstances extends OptionFunctions {

  implicit def optionEmpty[A]: Empty[Option[A]] = new Empty[Option[A]] {
    def empty = none
  }

  implicit def optEach[A]: Each[Option[A], A] = new Each[Option[A], A] {
    def each = some.asTraversal
  }

  implicit val noneEmpty: Empty[None.type] = new Empty[None.type] {
    def empty = Prism[None.type , Unit](_ => Some(()))(_ => None)
  }

}

