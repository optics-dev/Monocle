package monocle

import monocle.thirdparty.hlist._

object HListExample extends App {

  case class Example(i : Int, s: String, b: Boolean)

  val example = Example(1, "bla", true)

  import monocle.syntax.iso._

  val l = example <-> toHListIso get

  println( l ) // 1 :: "bla" :: true :: HNil

  println( _1.get(l) )        // 1
  println( _1.set(l, 2) )     // 2     :: "bla" :: true :: HNil
  println( _1.set(l, false) ) // false :: "bla" :: true :: HNil

  println( _2.get(l) )        // "bla"

}
