package monocle

import monocle.internal.focus.{FocusImpl, AppliedFocusImpl}
import monocle.syntax.FocusSyntax

object Focus extends FocusSyntax {

  extension [From, To] (from: From) 
    transparent inline def focus(inline lambda: (From => To)): Any = 
      ${AppliedFocusImpl[From, To]('from, 'lambda)}

  def apply[S] = new MkFocus[S]

  class MkFocus[From] {
    inline def apply(): Iso[From, From] =
      Iso.id

    transparent inline def apply[To](inline lambda: (From => To)): Any = 
      ${ FocusImpl('lambda) }
  }
}
