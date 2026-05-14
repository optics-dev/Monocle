package monocle.internal

import monocle.{Iso, PIso}
import scala.quoted.{quotes, Expr, Quotes, Type}
import scala.deriving.Mirror

object IsoFields {
  transparent inline def apply[S <: Product](using mirror: Mirror.ProductOf[S]): PIso[S, S, ? <: Tuple, ? <: Tuple] =
    ${ IsoFieldsImpl.apply[S]('mirror) }
}

private[monocle] object IsoFieldsImpl {

  def apply[S <: Product](mirror: Expr[Mirror.ProductOf[S]])(using Quotes, Type[S]): Expr[PIso[S, S, ? <: Tuple, ? <: Tuple]] =
    mirror match {
      case '{ type a <: Tuple; $m: Mirror.ProductOf[S] { type MirroredElemTypes = `a` } } =>
        '{
          val f: S => a = Tuple.fromProductTyped(_)(using $m)
          val g: a => S = $m.fromProduct(_)
          Iso[S, a](f)(g)
        }
    }
}
