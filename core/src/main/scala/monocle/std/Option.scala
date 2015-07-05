package monocle.std

import monocle.function._
import monocle.{PPrism, Prism, PIso, Iso}

import scalaz.{-\/, \/-, \/}

object option extends OptionOptics

trait OptionOptics {

  final def pSome[A, B]: PPrism[Option[A], Option[B], A, B] =
    PPrism[Option[A], Option[B], A, B](_.map(\/-(_)) getOrElse -\/(None))(Some.apply)

  final def some[A]: Prism[Option[A], A] =
    pSome[A, A]

  final def none[A]: Prism[Option[A], Unit] =
    Prism[Option[A], Unit]{ case None => Some(()); case Some(_) => None }(_ => None)

  final def pOptionToDisjunction[A, B]: PIso[Option[A], Option[B], Unit \/ A, Unit \/ B] =
    PIso[Option[A], Option[B], Unit \/ A, Unit \/ B](_.map(\/-(_)) getOrElse -\/(()))(_.toOption)

  final def optionToDisjunction[A]: Iso[Option[A], Unit \/ A] =
    pOptionToDisjunction[A, A]

  implicit def optionEmpty[A]: Empty[Option[A]] = new Empty[Option[A]] {
    def empty = none
  }

  implicit def optEach[A]: Each[Option[A], A] = new Each[Option[A], A] {
    def each = some.asTraversal
  }

}

