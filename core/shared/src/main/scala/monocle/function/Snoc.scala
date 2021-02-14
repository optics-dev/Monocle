package monocle.function

import cats.Applicative
import cats.instances.option._
import cats.syntax.either._
import monocle.{Iso, Optional, Prism}

import scala.annotation.{implicitNotFound, tailrec}

/** Typeclass that defines a [[Prism]] between an `S` and its init `S` and last `S`
  * @tparam S source of [[Prism]] and init of [[Prism]] target
  * @tparam A last of [[Prism]] target, `A` is supposed to be unique for a given `S`
  */
@implicitNotFound(
  "Could not find an instance of Snoc[${S},${A}], please check Monocle instance location policy to " + "find out which import is necessary"
)
@deprecated("no replacement", since = "3.0.0-M1")
abstract class Snoc[S, A] extends Serializable {
  def snoc: Prism[S, (S, A)]

  def initOption: Optional[S, S] = snoc.at(1)
  def lastOption: Optional[S, A] = snoc.at(2)
}

trait SnocFunctions {
  @deprecated("no replacement", since = "3.0.0-M1")
  final def snoc[S, A](implicit ev: Snoc[S, A]): Prism[S, (S, A)] = ev.snoc

  @deprecated("no replacement", since = "3.0.0-M1")
  final def initOption[S, A](implicit ev: Snoc[S, A]): Optional[S, S] =
    ev.initOption

  @deprecated("no replacement", since = "3.0.0-M1")
  final def lastOption[S, A](implicit ev: Snoc[S, A]): Optional[S, A] =
    ev.lastOption

  /** append an element to the end */
  @deprecated("no replacement", since = "3.0.0-M1")
  final def _snoc[S, A](init: S, last: A)(implicit ev: Snoc[S, A]): S =
    ev.snoc.reverseGet((init, last))

  /** deconstruct an S between its init and last */
  @deprecated("no replacement", since = "3.0.0-M1")
  final def _unsnoc[S, A](s: S)(implicit ev: Snoc[S, A]): Option[(S, A)] =
    ev.snoc.getOption(s)
}

object Snoc extends SnocFunctions {
  def apply[S, A](prism: Prism[S, (S, A)]): Snoc[S, A] =
    new Snoc[S, A] {
      override val snoc: Prism[S, (S, A)] = prism
    }

  /** lift an instance of [[Snoc]] using an [[Iso]] */
  def fromIso[S, A, B](iso: Iso[S, A])(implicit ev: Snoc[A, B]): Snoc[S, B] =
    Snoc(
      iso.andThen(ev.snoc).andThen(iso.reverse.first[B])
    )

  /** *********************************************************************************************
    */
  /** Std instances */
  /** *********************************************************************************************
    */
  implicit def listSnoc[A]: Snoc[List[A], A] =
    Snoc(
      Prism[List[A], (List[A], A)](s =>
        Applicative[Option].map2(Either.catchNonFatal(s.init).toOption, s.lastOption)((_, _))
      ) { case (init, last) =>
        init :+ last
      }
    )

  implicit def lazyListSnoc[A]: Snoc[LazyList[A], A] =
    Snoc(
      Prism[LazyList[A], (LazyList[A], A)](s =>
        for {
          init <- if (s.isEmpty) None else Some(s.init)
          last <- s.lastOption
        } yield (init, last)
      ) { case (init, last) =>
        init :+ last
      }
    )

  implicit val stringSnoc: Snoc[String, Char] = Snoc(
    Prism[String, (String, Char)](s => if (s.isEmpty) None else Some((s.init, s.last))) { case (init, last) =>
      init :+ last
    }
  )

  implicit def vectorSnoc[A]: Snoc[Vector[A], A] =
    Snoc(
      Prism[Vector[A], (Vector[A], A)](v => if (v.isEmpty) None else Some((v.init, v.last))) { case (xs, x) =>
        xs :+ x
      }
    )

  /** *********************************************************************************************
    */
  /** Cats instances */
  /** *********************************************************************************************
    */
  import cats.data.Chain

  implicit def chainSnoc[A]: Snoc[Chain[A], A] =
    new Snoc[Chain[A], A] {
      val snoc = Prism[Chain[A], (Chain[A], A)] { c =>
        @tailrec
        def go(oldC: Chain[A], newC: Chain[A]): Option[(Chain[A], A)] =
          oldC.uncons match {
            case Some((h, t)) if t.isEmpty => Some((newC, h))
            case Some((h, t))              => go(t, newC.append(h))
            case None                      => None
          }

        go(c, Chain.empty)
      } { case (init, last) =>
        init.append(last)
      }
    }
}
