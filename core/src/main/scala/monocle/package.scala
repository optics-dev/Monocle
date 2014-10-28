import scalaz.Maybe

package object monocle {

  type Optic[P[_, _], S, T, A, B] = P[A, B] => P[S, T]

  type SimpleSetter[S, A]    = Setter[S, S, A, A]
  type SimpleTraversal[S, A] = Traversal[S, S, A, A]
  type SimpleOptional[S, A]  = Optional[S, S, A, A]
  type SimplePrism[S, A]     = Prism[S, S, A, A]
  type Lens[S, A]            = PLens[S, S, A, A]
  type SimpleIso[S, A]       = Iso[S, S, A, A]


  object SimpleSetter {
    @inline def apply[S, A](modify: (A => A) => (S => S)): SimpleSetter[S, A] =
      Setter(modify)
  }

  object SimpleOptional {
    @inline def apply[S, A](_getMaybe: S => Maybe[A])(_set: (A, S) => S): SimpleOptional[S, A] =
      Optional{s: S => _getMaybe(s) \/> s}( _set)
  }

  object SimplePrism {
    @inline def apply[S, A](_getMaybe: S => Maybe[A])(_reverseGet: A => S): SimplePrism[S, A] =
      Prism{s: S => _getMaybe(s) \/> s}(_reverseGet)
  }



  object SimpleIso {
    @inline def apply[S, A](_get: S => A)(_reverseGet: A => S): SimpleIso[S, A] =
      Iso(_get)(_reverseGet)

    @inline def dummy[S]: SimpleIso[S, S] =
      SimpleIso[S, S](identity)(identity)
  }

}
