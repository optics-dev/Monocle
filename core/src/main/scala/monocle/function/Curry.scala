package monocle.function

import monocle.SimpleIso
import scala.annotation.implicitNotFound

@implicitNotFound("Could not find an instance of Curry[${F},${G}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Curry[F, G] {

  /** curry: ((A,B,...,Z) => Res) <=> (A => B => ... => Z => Res) */
  def curry: SimpleIso[F, G]
}

object Curry extends CurryFunctions

trait CurryFunctions {

  def curry[F, G](implicit ev: Curry[F, G]): SimpleIso[F, G] = ev.curry

  def uncurry[F, G](implicit ev: Curry[F, G]): SimpleIso[G, F] = curry.reverse

}
