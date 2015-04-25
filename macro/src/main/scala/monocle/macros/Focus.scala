package monocle.macros

import scala.language.dynamics

import monocle._
import monocle.syntax.ApplyLens

/** Syntactic sugar around an ApplyLens */
class Focus[S,A](val applyLens: ApplyLens[S,S,A,A]) extends AnyVal with Dynamic{
  def apply[C](field: A => C): Focus[S,C] = macro internal.FocusImpl.apply[S,A,C]

  /**
  Whitebox macro for field access syntax.
  E.g. someobj.focus.a.b...
  */
  def selectDynamic(field: String): Any = macro internal.FocusImpl.selectDynamicImpl
  
  /**
  Assignment syntax for updates
  E.g. someobj.focus(_.a.b) = ...
  */
  def update[C](field: A => C, value: C): S = macro internal.FocusImpl.update[S,A,C]
  /**
  Assignment syntax for updates with member access syntax
  E.g. someobj.focus.a.b = ...
  */
  def updateDynamic[C](field: String)(value: C): S = macro internal.FocusImpl.updateDynamicImpl[S,C]
  
  def get: A = applyLens.get
  def set(value: A): S = applyLens.set(value)
  def modify(diff: A => A): S = applyLens.modify(diff)
}
object Focus{
  def apply[S](value: S) = new Focus(ApplyLens[S,S,S,S]( value, Lens.id ))
}
