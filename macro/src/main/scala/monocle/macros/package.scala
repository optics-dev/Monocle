package monocle.macros
import monocle.syntax.ApplyLens
import monocle._
object `package`{
  /**
  Function-style Focus constructor for any object
  E.g. focus(someobj)...
  */
  def focus[S](value: S) = Focus(value)
  implicit class FocusOps[S](value: S){
    /**
    Inline Focus constructor for any object
    E.g. someobj.focus...
    */
    def focus = new Focus(ApplyLens[S,S,S,S]( value, Lens.id ))
  }
}
