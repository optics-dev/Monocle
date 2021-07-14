package monocle.syntax

import monocle._
import monocle.internal.focus.ComposedFocusImpl

trait ComposedFocusSyntax {

  extension [S, A, Next] (optic: Lens[S, A]) {
   transparent inline def refocus(inline lambda: (Focus.KeywordContext ?=> A => Next)): Any =
     ${ComposedFocusImpl[S, A, Next]('optic, 'lambda)}
  }

  // extension [S, A, Next] (optic: AppliedLens[S, A]) {
  //  transparent inline def refocus(inline lambda: (Focus.KeywordContext ?=> A => Next)): Any =
  //    ${AppliedFocusImpl[S, A, Next]('optic, 'lambda)}
  // }
  
  extension [S, A, Next] (optic: Iso[S, A]) {
    transparent inline def refocus(inline lambda: (Focus.KeywordContext ?=> A => Next)): Any =
      ${ComposedFocusImpl[S, A, Next]('optic, 'lambda)}
  }

/*
  extension [From, To] (optic: Prism[From, To]) {
    transparent inline def refocus(inline lambda: (Focus.KeywordContext ?=> From => To)): Any =
      optic.andThen(${FocusImpl[From, To]('lambda)})
  }

  extension [From, To] (optic: Optional[From, To]) {
    transparent inline def refocus(inline lambda: (Focus.KeywordContext ?=> From => To)): Any =
      optic.andThen(${FocusImpl[From, To]('lambda)})
  }


  extension [From, To] (optic: Traversal[From, To]) {
    transparent inline def refocus(inline lambda: (Focus.KeywordContext ?=> From => To)): Any =
      optic.andThen(${FocusImpl[From, To]('lambda)})
  }

  extension [From, To] (optic: Getter[From, To]) {
    transparent inline def refocus(inline lambda: (Focus.KeywordContext ?=> From => To)): Any =
      optic.andThen(${FocusImpl[From, To]('lambda)})
  }

  extension [From, To] (optic: Setter[From, To]) {
    transparent inline def refocus(inline lambda: (Focus.KeywordContext ?=> From => To)): Any =
      optic.andThen(${FocusImpl[From, To]('lambda)})
  }*/

}
