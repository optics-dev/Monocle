package monocle.function

import monocle.Iso
import scala.annotation.implicitNotFound

@implicitNotFound(
  "Could not find an instance of Curry[${F},${G}], please check Monocle instance location policy to " + "find out which import is necessary"
)
abstract class Curry[F, G] extends Serializable {

  /** curry: ((A,B,...,Z) => Res) <=> (A => B => ... => Z => Res) */
  def curry: Iso[F, G]
}

trait CurryFunctions {
  def curry[F, G](implicit ev: Curry[F, G]): Iso[F, G]   = ev.curry
  def uncurry[F, G](implicit ev: Curry[F, G]): Iso[G, F] = curry.reverse
}

object Curry extends CurryFunctions with CurryInstances {
  def apply[F, G](iso: Iso[F, G]): Curry[F, G] = new Curry[F, G] {
    val curry: Iso[F, G] = iso
  }

  implicit def curry5[A, B, C, D, E, F] = Curry(
    Iso((_: (A, B, C, D, E) => F).curried)(f => Function.uncurried(f))
  )
}

trait CurryInstances extends CurryInstances1 {
  implicit def curry4[A, B, C, D, E] = Curry(
    Iso((_: (A, B, C, D) => E).curried)(f => Function.uncurried(f))
  )
}

trait CurryInstances1 extends CurryInstances2 {
  implicit def curry3[A, B, C, D] = Curry(
    Iso((_: (A, B, C) => D).curried)(f => Function.uncurried(f))
  )
}

trait CurryInstances2 {
  implicit def curry2[A, B, C] = Curry(
    Iso((_: (A, B) => C).curried)(f => Function.uncurried(f))
  )
}
