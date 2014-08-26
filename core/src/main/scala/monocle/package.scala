import scalaz.{Maybe, -\/, \/-}
import scala.util.Try

package object monocle {

  type SimpleSetter[S, A]    = Setter[S, S, A, A]
  type SimpleTraversal[S, A] = Traversal[S, S, A, A]
  type SimpleLens[S, A]      = Lens[S, S, A, A]
  type SimpleIso[S, A]       = Iso[S, S, A, A]
  type SimpleOptional[S, A]  = Optional[S, S, A, A]
  type SimplePrism[S, A]     = Prism[S, S, A, A]

  object SimpleLens {
    def apply[S, A](_get: S => A, _set: (S, A) => S): SimpleLens[S, A] =
      Lens[S, S, A, A](_get, _set)

    /** Alternative syntax that allows the field type to be inferred rather and explicitly specified. */
    def apply[S]: Constructor[S] = new Constructor[S]
    final class Constructor[S] {
      @inline def apply[A](_get: S => A)(_set: (S, A) => S): SimpleLens[S, A] = Lens[S, S, A, A](_get, _set)
    }
  }

  object SimpleOptional {
    def apply[S, A](_getMaybe: S => Maybe[A], _set: (S, A) => S): SimpleOptional[S, A] =
      Optional[S, S, A, A](s => _getMaybe(s) \/> s, _set)

    /** Alternative syntax that allows the field type to be inferred rather and explicitly specified. */
    def apply[S]: Constructor[S] = new Constructor[S]
    final class Constructor[S] {
      @inline def apply[A](_getMaybe: S => Maybe[A])(_set: (S, A) => S): SimpleOptional[S, A] =
        SimpleOptional(_getMaybe, _set)
    }
  }

  object SimpleIso {
    def apply[S, A](_get: S => A, _reverseGet: A => S): SimpleIso[S, A] = Iso(_get, _reverseGet)
    def dummy[S]: SimpleIso[S, S] = SimpleIso(identity, identity)

    /** Alternative syntax that allows the field type to be inferred rather and explicitly specified. */
    def apply[S]: Constructor[S] = new Constructor[S]
    final class Constructor[S] {
      @inline def apply[A](_get: S => A)(_reverseGet: A => S): SimpleIso[S, A] = Iso[S, S, A, A](_get, _reverseGet)
    }
  }

  object SimplePrism {
    def apply[S, A](_getMaybe: S => Maybe[A], _reverseGet: A => S): SimplePrism[S, A] =
      Prism( s => _getMaybe(s) \/> s, _reverseGet)

    /** Alternative syntax that allows the field type to be inferred rather and explicitly specified. */
    def apply[A]: Constructor[A] = new Constructor[A]
    final class Constructor[A] {
      @inline def apply[S](_getMaybe: S => Maybe[A])(_reverseGet: A => S) = SimplePrism[S, A](_getMaybe, _reverseGet)
    }
  }

}
