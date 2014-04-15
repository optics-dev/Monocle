package monocle.function

import monocle.SimpleIso

trait Curry[F, G] {
  def curry: SimpleIso[F, G]
}

object Curry extends CurryInstances

trait CurryInstances {
  implicit def curry2[A,B,C] = new Curry[(A,B) => C,A => B => C] {
    def curry = SimpleIso(_.curried, f => Function.uncurried(f))
  }

  implicit def curry3[A,B,C,D] = new Curry[(A,B,C) => D, A => B => C => D] {
    def curry = SimpleIso(_.curried, f => Function.uncurried(f))
  }

  implicit def curry4[A,B,C,D,E] = new Curry[(A,B,C,D) => E, A => B => C => D => E] {
    def curry = SimpleIso(_.curried, f => Function.uncurried(f))
  }

  implicit def curry5[A,B,C,D,E,F] = new Curry[(A,B,C,D,E) => F, A => B => C => D => E => F] {
    def curry = SimpleIso(_.curried, f => Function.uncurried(f))
  }
}
