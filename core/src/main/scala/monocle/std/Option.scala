package monocle.std

import monocle._
import scala.Some
import scalaz.-\/
import scalaz.\/-


object option extends OptionInstances

trait OptionInstances {
  def _Some[A, B]: Prism[Option[A], Option[B], A, B] =
    Prism[Option[A], Option[B], A, B](Some.apply, _.map(\/-(_)) getOrElse -\/(None))

  def _None[A]: SimplePrism[Option[A] , Unit] =
    SimplePrism[Option[A] , Unit](_ => None, { opt => if(opt == None) Some(()) else None } )
}
