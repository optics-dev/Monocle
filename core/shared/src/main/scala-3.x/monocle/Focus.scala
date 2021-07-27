package monocle

import monocle.syntax.{AppliedFocusSyntax, ComposedFocusSyntax}
import monocle.internal.focus.FocusImpl
import monocle.function.{Each, At, Index}

object Focus extends AppliedFocusSyntax with ComposedFocusSyntax {

  sealed trait KeywordContext {
    extension [From] (from: From)
      def as[CastTo <: From]: CastTo = scala.sys.error("Extension method 'as[CastTo]' should only be used within the monocle.Focus macro.")

    extension [A] (opt: Option[A])
      def some: A = scala.sys.error("Extension method 'some' should only be used within the monocle.Focus macro.")

    extension [From, To] (from: From)(using Each[From, To])
      def each: To = scala.sys.error("Extension method 'each' should only be used within the monocle.Focus macro.")

    extension [From, I, To] (from: From)
      def at(i: I)(using At[From, i.type, To]): To = scala.sys.error("Extension method 'at(i)' should only be used within the monocle.Focus macro.")

    extension [From, I, To] (from: From)
      def index(i: I)(using Index[From, I, To]): To = scala.sys.error("Extension method 'index(i)' should only be used within the monocle.Focus macro.")

    extension [A] (from: Option[A])
      def withDefault(defaultValue: A): A = scala.sys.error("Extension method 'withDefault(value)' should only be used within the monocle.Focus macro.")
  }

  def apply[S] = new MkFocus[S]

  class MkFocus[From] {
    def apply(): Iso[From, From] = Iso.id

    transparent inline def apply[To](inline lambda: (KeywordContext ?=> From => To)): Any = 
      ${ FocusImpl('lambda) }
  }
}
