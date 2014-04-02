package monocle.std

import monocle.util.Each
import monocle.{ SimpleTraversal, SimplePrism, Prism }
import scalaz.{ -\/, \/- }

object option extends OptionInstances

trait OptionInstances {
  def some[A, B]: Prism[Option[A], Option[B], A, B] =
    Prism[Option[A], Option[B], A, B](Some.apply, _.map(\/-(_)) getOrElse -\/(None))

  def none[A]: SimplePrism[Option[A], Unit] =
    SimplePrism[Option[A], Unit](_ => None, { opt => if (opt == None) Some(()) else None })

  implicit def optEachInstance[A]: Each.Aux[Option[A], A] = new Each[Option[A]] {
    type IN = A
    def each: SimpleTraversal[Option[A], A] = some[A, A]
  }
}
