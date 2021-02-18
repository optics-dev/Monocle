package monocle.syntax

import monocle.Focus
import monocle.internal.focus.AppliedFocusImpl

trait AppliedFocusSyntax {

  extension [From, To] (from: From) 
    transparent inline def focus(inline lambda: (Focus.MagicKeywords ?=> From => To)): Any = 
      ${AppliedFocusImpl[From, To]('from, 'lambda)}
}
