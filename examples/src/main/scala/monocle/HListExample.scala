package monocle

import monocle.thirdparty.hlist._
import shapeless._
import shapeless.Generic._

object HListExample extends App {

  val l = 1 :: "bla" :: true :: HNil

  println( _1.get(l) )        // 1
  println( _1.set(l, 2) )     // 2     :: "bla" :: true :: HNil
  println( _1.set(l, false) ) // false :: "bla" :: true :: HNil

  println( _2.get(l) )        // "bla"

  println( pairToHListIso.get((1,"bla")) ) // 1
  println( (pairToHListIso compose _1[Int, HL[String,HNil], Char]).set((1,"bla"), 'c') )  // ('c', "bla")


  case class Person(_name : String, _age : Int, _location: (Int, Int))
  val location = Macro.mkLens[Person, (Int, Int)]("_location")

  val character = Person("Bob", 27, (3,4))

  implicit val gen = Generic.product[Person]

  println( first.get(character) )         // Bob
  println( first.set(character, "Roger")) // Person("Roger", 27, (3,4))

  println( (location lensCompose first) get character  )

  // this does not compile!
  // println( (location compose first) get character  )

}
