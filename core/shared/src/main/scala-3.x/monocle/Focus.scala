package monocle

<<<<<<< HEAD
import monocle.function.Each
import monocle.internal.focus.{FocusImpl, AppliedFocusImpl}
import monocle.syntax.FocusSyntax
=======
import monocle.internal.focus.{FocusImpl, AppliedFocusImpl, InFocus}
>>>>>>> Use a dummy trait to restrict Focus actions within a context.

object Focus extends FocusSyntax {

  extension [From, To] (from: From)
    transparent inline def focus(inline lambda: (InFocus ?=> From => To)): Any =
      ${AppliedFocusImpl[From, To]('from, 'lambda)}

<<<<<<< HEAD
=======
  extension [CastTo] (from: Any)(using InFocus)
    def as: CastTo = scala.sys.error("Extension method 'as[CastTo]' should only be used within the monocle.Focus macro.")

  extension [A] (opt: Option[A])(using InFocus)
    def some: A = scala.sys.error("Extension method 'some' should only be used within the monocle.Focus macro.")

>>>>>>> Use a dummy trait to restrict Focus actions within a context.
  def apply[S] = new MkFocus[S]

  class MkFocus[From] {
    transparent inline def apply[To](inline lambda: (InFocus ?=> From => To)): Any =
      ${ FocusImpl('lambda) }
  }
}
