package monocle

import monocle.thirdparty.hlist._
import shapeless._

object HListExample extends App {

  val l = 1 :: "bla" :: true :: HNil

  println( _1.get(l) )        // 1
  println( _1.set(l, 2) )     // 2     :: "bla" :: true :: HNil
  println( _1.set(l, false) ) // false :: "bla" :: true :: HNil

  println( _2.get(l) )        // "bla"

  println( pairToHListIso.get((1,"bla")) ) // 1
  println( (pairToHListIso compose _1[Int, HL[String,HNil], Char]).set((1,"bla"), 'c') )  // ('c', "bla")


  case class Person(name : String, age : Int)
  val julien = Person("Julien", 27)

  println( first.get(julien) )



}
