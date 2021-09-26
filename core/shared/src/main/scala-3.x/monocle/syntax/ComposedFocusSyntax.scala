package monocle.syntax

import monocle._
import monocle.internal.focus.ComposedFocusImpl

trait ComposedFocusSyntax {

  extension [S, A, Next](optic: Setter[S, A] | Fold[S, A] | AppliedSetter[S, A] | AppliedFold[S, A]) {
    transparent inline def refocus(inline lambda: (Focus.KeywordContext ?=> A => Next)): Any =
      ${ ComposedFocusImpl[S, A, Next]('optic, 'lambda) }
  }
}
