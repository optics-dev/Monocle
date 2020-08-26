package monocle.std

import cats.implicits._
import cats.Eq
import monocle.{Iso, PIso, PPrism, Prism}

object option extends OptionOptics

trait OptionOptics {
  final def pSome[A, B]: PPrism[Option[A], Option[B], A, B] =
    PPrism[Option[A], Option[B], A, B](_.map(Right(_)) getOrElse Left(None))(
      Some.apply)

  final def some[A]: Prism[Option[A], A] =
    pSome[A, A]

  final def none[A]: Prism[Option[A], Unit] =
    Prism[Option[A], Unit] { case None => Some(()); case Some(_) => None }(_ =>
      None)

  final def pOptionToDisjunction[A, B]
    : PIso[Option[A], Option[B], Either[Unit, A], Either[Unit, B]] =
    PIso[Option[A], Option[B], Either[Unit, A], Either[Unit, B]](
      _.map(Right(_)) getOrElse Left(()))(_.toOption)

  final def optionToDisjunction[A]: Iso[Option[A], Either[Unit, A]] =
    pOptionToDisjunction[A, A]

  /**
    * Creates an Iso that maps `None` to defaultValue` and inversely.
    * {{{
    * val defaultTo0 = withDefault(0)
    * defaultTo0.get(None) == 0
    * defaultTo0.get(Some(1)) == 1
    * defaultTo0.reverseGet(0) == None
    * defaultTo0.reverseGet(1) == Some(1)
    * }}}
    * This is only a valid Iso if we consider the set of A without defaultValue.
    * For example, `Some(0)` breaks the round-trip property of Iso:
    * {{{
    * defaultTo0.reverseGet(defaultTo0.get(Some(0))) == None
    * }}}
    *
    * @see This method is called `non` in Haskell Lens.
    **/
  final def withDefault[A: Eq](defaultValue: A): Iso[Option[A], A] =
    Iso[Option[A], A](_.getOrElse(defaultValue))(value =>
      if (value === defaultValue) None else Some(value))
}
