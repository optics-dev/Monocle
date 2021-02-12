package monocle

import monocle.internal.focus.{FocusImpl, AppliedFocusImpl}

object Focus {

  extension [From, To] (from: From) 
    transparent inline def focus(inline lambda: (From => To)): Any = 
      ${AppliedFocusImpl[From, To]('from, 'lambda)}

  extension [CastTo] (from: Any)
    def as: CastTo = scala.sys.error("Extension method 'as[CastTo]' should only be used within the monocle.Focus macro.")

  extension [A] (opt: Option[A])
    def some: A = scala.sys.error("Extension method 'some' should only be used within the monocle.Focus macro.")
  
  def apply[S] = new MkFocus[S]

  class MkFocus[From] {
    transparent inline def apply[To](inline lambda: (From => To)): Any = 
      ${ FocusImpl('lambda) }
  }
}
