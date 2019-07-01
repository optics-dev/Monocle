package monocle.function

import monocle.function.fields._
import monocle.{Iso, Optional, Prism}

import scala.annotation.{implicitNotFound, tailrec}
import cats.Applicative
import cats.instances.option._
import cats.syntax.either._

/**
 * Typeclass that defines a [[Prism]] between an `S` and its init `S` and last `S`
 * @tparam S source of [[Prism]] and init of [[Prism]] target
 * @tparam A last of [[Prism]] target, `A` is supposed to be unique for a given `S`
 */
@implicitNotFound("Could not find an instance of Snoc[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
abstract class Snoc[S, A] extends Serializable {
  def snoc: Prism[S, (S, A)]

  def initOption: Optional[S, S] = snoc composeLens first
  def lastOption: Optional[S, A] = snoc composeLens second
}

trait SnocFunctions {
  final def snoc[S, A](implicit ev: Snoc[S, A]): Prism[S, (S, A)] = ev.snoc

  final def initOption[S, A](implicit ev: Snoc[S, A]): Optional[S, S] = ev.initOption
  final def lastOption[S, A](implicit ev: Snoc[S, A]): Optional[S, A] = ev.lastOption

  /** append an element to the end */
  final def _snoc[S, A](init: S, last: A)(implicit ev: Snoc[S, A]): S =
    ev.snoc.reverseGet((init, last))

  /** deconstruct an S between its init and last */
  final def _unsnoc[S, A](s: S)(implicit ev: Snoc[S, A]): Option[(S, A)] =
    ev.snoc.getOption(s)
}

object Snoc extends SnocFunctions with SnocInstancesScalaVersionSpecific {

  def apply[S, A](prism: Prism[S, (S, A)]): Snoc[S, A] = new Snoc[S, A] {
    override val snoc: Prism[S, (S, A)] = prism
  }

  /** lift an instance of [[Snoc]] using an [[Iso]] */
  def fromIso[S, A, B](iso: Iso[S, A])(implicit ev: Snoc[A, B]): Snoc[S, B] = Snoc(
    iso composePrism ev.snoc composeIso iso.reverse.first
  )

  /************************************************************************************************/
  /** Std instances                                                                               */
  /************************************************************************************************/

  implicit def listSnoc[A]: Snoc[List[A], A] = Snoc(
    Prism[List[A], (List[A], A)](
      s => Applicative[Option].map2(Either.catchNonFatal(s.init).toOption, s.lastOption)((_,_))){
      case (init, last) => init :+ last
    }
  )

  implicit val stringSnoc: Snoc[String, Char] = Snoc(
    Prism[String, (String, Char)](
        s => if(s.isEmpty) None else Some((s.init, s.last))){
        case (init, last) => init :+ last
      }
  )

  implicit def vectorSnoc[A]: Snoc[Vector[A], A] = Snoc(
    Prism[Vector[A], (Vector[A], A)](
      v => if(v.isEmpty) None else Some((v.init, v.last))){
      case (xs, x) => xs :+ x
    }
  )

  /************************************************************************************************/
  /** Cats instances                                                                              */
  /************************************************************************************************/
  import cats.data.Chain

  implicit def chainSnoc[A]: Snoc[Chain[A], A] = new Snoc[Chain[A], A] {
    val snoc = Prism[Chain[A], (Chain[A], A)] {
      c =>
        @tailrec
        def go(oldC: Chain[A], newC: Chain[A]): Option[(Chain[A], A)] =
          oldC.uncons match {
            case Some((h, t)) if t.isEmpty => Some((newC, h))
            case Some((h, t))              => go(t, newC.append(h))
            case None                      => None
          }

        go(c, Chain.empty)
    } {
      case (init, last) => init.append(last)
    }
  }
}
