package monocle

import monocle.internal.focus.FocusImpl

object Focus {

  extension [A] (opt: Option[A])
    def some: A = scala.sys.error("Extension method 'some' should only be used within the moocle.Focus macro.")
  
  def apply[S] = new MkFocus[S]

  class MkFocus[S] {
    transparent inline def apply[T](inline get: (S => T)): Any = 
      ${ FocusImpl('get) }
  }
}
