package monocle.syntax

<<<<<<< HEAD
import monocle.function.Each

trait FocusSyntax {
=======
sealed trait FocusSyntax {
>>>>>>> Having our cake and eating it
  extension [CastTo] (from: Any)
    def as: CastTo = scala.sys.error("Extension method 'as[CastTo]' should only be used within the monocle.Focus macro.")

  extension [A] (opt: Option[A])
    def some: A = scala.sys.error("Extension method 'some' should only be used within the monocle.Focus macro.")

  extension [From, To] (from: From)(using Each[From, To])
    def each: To = scala.sys.error("Extension method 'each' should only be used within the monocle.Focus macro.")
}
