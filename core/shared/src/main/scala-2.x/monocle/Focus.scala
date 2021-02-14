package monocle

import monocle.syntax.FocusSyntax

object Focus extends FocusSyntax {

  class MkFocus[From] {
    def apply(): Iso[From, From] =
      Iso.id
  }

}
