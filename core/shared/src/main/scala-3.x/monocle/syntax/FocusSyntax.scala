package monocle.syntax

trait FocusSyntax {
  extension [CastTo] (from: Any)
    def as: CastTo = scala.sys.error("Extension method 'as[CastTo]' should only be used within the monocle.Focus macro.")

  extension [A] (opt: Option[A])
   def some: A = scala.sys.error("Extension method 'some' should only be used within the monocle.Focus macro.")
}
