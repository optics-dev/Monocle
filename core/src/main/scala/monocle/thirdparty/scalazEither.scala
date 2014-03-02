package monocle.thirdparty

import monocle.Prism
import scalaz.{\/-, -\/, \/}

object scalazEither extends ScalazEitherInstances

trait ScalazEitherInstances {
  def _Left[A, B, C]: Prism[A \/ B, C \/ B, A, C] =
    Prism[A \/ B, C \/ B, A, C](-\/.apply, {
      case -\/(a) => \/-(a)
      case \/-(b) => -\/(\/-(b))
    } )

  def _Right[A, B, C]: Prism[A \/ B, A \/ C, B, C] =
    Prism[A \/ B, A \/ C, B, C](\/-.apply, {
      case -\/(a) => -\/(-\/(a))
      case \/-(b) => \/-(b)
    } )
}
