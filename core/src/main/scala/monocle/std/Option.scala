package monocle.std

import monocle.{ SimplePrism, Prism, Iso }
import scalaz.{ -\/, \/- }

object option extends OptionInstances

trait OptionInstances {

  def some[A, B]: Prism[Option[A], Option[B], A, B] =
    Prism[Option[A], Option[B], A, B](Some.apply, _.map(\/-(_)) getOrElse -\/(None))

  def none[A]: SimplePrism[Option[A], Unit] =
    SimplePrism[Option[A], Unit](_ => None, { opt => if (opt == None) Some(()) else None })

  def someIso[A, B]: Iso[Some[A], Some[B], A, B] =
    Iso[Some[A], Some[B], A, B](_.get, Some(_))

}
