package monocle.internal

import monocle.Iso
import scala.quoted.{quotes, Expr, Quotes, Type}
import scala.deriving.Mirror

object IsoFields {
  transparent inline def apply[S <: Product](using mirror: Mirror.ProductOf[S]): Iso[S, Tuple] =
    ${ IsoFieldsImpl.apply[S]('mirror) }
}

private[monocle] object IsoFieldsImpl {

  def apply[S <: Product](mirror: Expr[Mirror.ProductOf[S]])(using Quotes, Type[S]): Expr[Iso[S, Tuple]] = {
    import quotes.reflect.*

    def whitebox[A <: Tuple](e: Expr[Iso[S, A]]): Expr[Iso[S, Tuple]] =
      e.asInstanceOf[Expr[Iso[S, Tuple]]]

    mirror match {
      case '{ type a <: Tuple; $m: Mirror.ProductOf[S] { type MirroredElemTypes = `a` } } =>
        whitebox('{
          val f: S => a = Tuple.fromProductTyped(_)(using $m)
          val g: a => S = $m.fromProduct(_)
          Iso[S, a](f)(g)
        })
    }
  }
}
