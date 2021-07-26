package monocle.syntax

import monocle._
import monocle.internal.focus.ComposedFocusImpl

trait ComposedFocusSyntax {

  extension [S, A, Next] (optic: Setter[S, A] | Fold[S,A]) {
    transparent inline def refocus(inline lambda: (Focus.KeywordContext ?=> A => Next)): Any = 
      ${ComposedFocusImpl[S, A, Next]('optic, 'lambda)}
  }

  extension [S, A, Next] (optic: AppliedSetter[S, A] | AppliedFold[S,A]) {
    transparent inline def refocus(inline lambda: (Focus.KeywordContext ?=> A => Next)): Any =
      ${ComposedFocusImpl.applied[S, A, Next]('optic, 'lambda)}
  }
}