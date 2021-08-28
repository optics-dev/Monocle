package monocle.macros

import monocle.Iso
import scala.language.`3.0`
import scala.deriving.*
import scala.quoted.*

object GenIso {

  /** Generate an [[Iso]] between a case class `S` and its unique field of type `A`. */
  @deprecated("Use monocle.Focus[S](_.singleField) to create a single field Iso", since = "3.1.0")
  inline def apply[S <: Product, A](using m: Mirror.ProductOf[S]): Iso[S, A] =
    ${ _apply[S, A]('m) }

  private def _apply[S <: Product, A](e: Expr[Mirror.ProductOf[S]])(using Quotes, Type[S], Type[A]): Expr[Iso[S, A]] = {
    import quotes.reflect.*

    e match {
      case '{ $m: Mirror.ProductOf[S] {type MirroredElemTypes = A *: EmptyTuple} } =>
        '{
          val f: S => A = Tuple.fromProductTyped(_)(using $m).asInstanceOf[Tuple1[A]]._1
          val g: A => S = a => $m.fromProduct(a *: EmptyTuple)
          Iso[S, A](f)(g)
        }

      case '{ $m: Mirror.ProductOf[S] {type MirroredElemTypes = a} } =>
        report.throwError(s"Can't generate an Iso[${Type.show[S]}, ${Type.show[A]}] because it's fields are ${TypeRepr.of[a].show} ")
    }
  }

  /** Generate an [[Iso]] between an object `S` and `Unit`. */
  @deprecated("no replacement", since = "3.1.0")
  inline def unit[S <: Product](using m: Mirror.ProductOf[S]): Iso[S, Unit] =
    ${ _unit[S]('m) }

  private def _unit[S <: Product](e: Expr[Mirror.ProductOf[S]])(using Quotes, Type[S]): Expr[Iso[S, Unit]] = {
    import quotes.reflect.*

    e match {
      case '{ $m: Mirror.ProductOf[S] {type MirroredElemTypes = EmptyTuple} } =>
        '{
          val s: S = $m.fromProduct(EmptyTuple)
          Iso[S, Unit](_ => ())(_ => s)
        }

      case '{ $m: Mirror.ProductOf[S] {type MirroredElemTypes = a} } =>
        report.throwError(s"Can't generate an Iso[${Type.show[S]}, Unit] because it's fields are ${TypeRepr.of[a].show} ")
    }
  }

  /** Generate an [[Iso]] between a case class `S` and its fields.
    *
    * Case classes with 0 fields will correspond with `Unit`, 1 with the field type, 2 or more with
    * a tuple of all field types in the same order as the fields themselves.
    */
  @deprecated("use monocle.Iso.fields", since = "3.1.0")
  transparent inline def fields[S <: Product](using m: Mirror.ProductOf[S]): Iso[S, Any] =
    ${ _fields[S]('m) }

  private def _fields[S <: Product](e: Expr[Mirror.ProductOf[S]])(using Quotes, Type[S]): Expr[Iso[S, Any]] = {
    import quotes.reflect.*
    
    def whitebox[A](e: Expr[Iso[S, A]]): Expr[Iso[S, Any]] =
      e.asInstanceOf[Expr[Iso[S, Any]]]

    e match {
      case '{ $m: Mirror.ProductOf[S] {type MirroredElemTypes = EmptyTuple} } =>
        whitebox(_unit[S](e))

      case '{ $m: Mirror.ProductOf[S] {type MirroredElemTypes = a *: EmptyTuple} } =>
        whitebox(_apply[S, a](e))

      case _ =>
        whitebox(monocle.internal.IsoFields[S](e))
    }
  }
}
