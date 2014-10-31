package monocle.std

import monocle.Prism

import scalaz.\&/.{Both, That, This}
import scalaz.{\/-, -\/, \&/, \/, Maybe}
import scalaz.syntax.maybe._
import scalaz.syntax.either._

object these extends TheseFunctions

trait TheseFunctions {
  def theseDisjunction[A, B]: Prism[A \&/ B, A \/ B] = Prism[A \&/ B, A \/ B]{
    case This(a) => a.left[B].just
    case That(b) => b.right[A].just
    case Both(_, _) => Maybe.empty[A \/ B]
  }{
    case -\/(a) => This(a)
    case \/-(b) => That(b)
  }
}
