import scala.util.Try
import scalaz.{ -\/, \/- }

package object monocle {

  type SimpleSetter[S, A] = Setter[S, S, A, A]
  type SimpleTraversal[S, A] = Traversal[S, S, A, A]
  type SimpleLens[S, A] = Lens[S, S, A, A]
  type SimpleIso[S, A] = Iso[S, S, A, A]
  type SimplePrism[S, A] = Prism[S, S, A, A]

  object SimpleLens {
    def apply[S, A](_get: S => A, _set: (S, A) => S): SimpleLens[S, A] = Lens[S, S, A, A](_get, _set)
  }

  object SimpleIso {
    def apply[S, A](_get: S => A, _reverseGet: A => S): SimpleIso[S, A] = Iso(_get, _reverseGet)
    def dummy[S]: SimpleIso[S, S] = SimpleIso(identity, identity)
  }

  object SimplePrism {
    def apply[S, A](_reverseGet: A => S, _getOption: S => Option[A]): SimplePrism[S, A] =
      Prism(_reverseGet, { s: S => _getOption(s).map(\/-(_)) getOrElse -\/(s) })

    def trySimplePrism[S, A](safe: A => S, unsafe: S => A): SimplePrism[S, A] =
      SimplePrism(safe, s => Try(unsafe(s)).toOption)
  }

  implicit final class SimplePrismOps[S, A](prism: SimplePrism[S, A]){
    def reverseModify(from: A, f: S => S): Option[A] =  prism.getOption(f(prism.reverseGet(from)))
  }

}
