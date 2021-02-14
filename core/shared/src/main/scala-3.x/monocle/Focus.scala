package monocle

import monocle.internal.focus.{FocusImpl, AppliedFocusImpl}
import monocle.syntax.FocusSyntax

object Focus {

  extension [From, To] (from: From) 
    transparent inline def focus(inline lambda: (FocusSyntax ?=> From => To)): Any = 
      ${AppliedFocusImpl[From, To]('from, 'lambda)}

  def apply[S] = new MkFocus[S]

  class MkFocus[From] {
    transparent inline def apply[To](inline lambda: (FocusSyntax ?=> From => To)): Any = 
      ${ FocusImpl('lambda) }
  }
}
