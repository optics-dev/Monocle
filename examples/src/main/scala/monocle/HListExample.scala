package monocle

import monocle.thirdparty.hlist._
import shapeless.HNil

object HListExample extends App {

  val l = 1 :: "bla" :: true :: HNil

  println(_1.get(l)) // 1
  println(_1.set(l, 2)) // 2     :: "bla" :: true :: HNil
  println(_1.set(l, false)) // false :: "bla" :: true :: HNil

  println(_2.get(l)) // "bla"

}
