package monocle.syntax

import monocle.{Focus, Iso}
import monocle.internal.focus.AppliedFocusImpl

trait AppliedFocusSyntax {

  extension [From, To] (from: From) {
    def focus(): ApplyIso[From, From, From, From] = ApplyIso(from, Iso.id)

    transparent inline def focus(inline lambda: (Focus.KeywordContext ?=> From => To)): Any =
      ${AppliedFocusImpl[From, To]('from, 'lambda)}
  }

}
