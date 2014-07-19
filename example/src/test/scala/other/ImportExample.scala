package other

import org.specs2.scalaz.Spec
import shapeless.test.illTyped
import scalaz.IList
import shapeless.{HNil, ::}

case class Custom(value: Int)

object Custom {

  import monocle.SimpleLens
  import monocle.function.Head

  implicit val customHead = new Head[Custom, Int]{
    def head = SimpleLens[Custom](_.value)((c, v) => c.copy(value = v))
  }
}

class ImportExample extends Spec {

  "monocle.function._ can be used to get all polymorphic optics" in {
    import monocle.function._

    // do not compile because Each instance for List is not in the scope
    illTyped { """each[List[Int], Int].modify(List(1,2,3), _ + 1)""" }

    import monocle.std.list._
    each[List[Int], Int].modify(List(1,2,3), _ + 1) shouldEqual List(2,3,4)

    // also compile because the instance is in the companion object Custom
    head[Custom, Int].modify(Custom(1), _ + 1) shouldEqual Custom(2)
  }

  "monocle.std._ brings all polymorphic Optic instances in scope for standard Scala classes" in {
    import monocle.function._
    import monocle.std._

    // do not compile because Each instance for IList is not in scope
    illTyped { """each[IList[Int], Int].modify(IList(1,2,3), _ + 1)""" }

    each[List[Int], Int].modify(List(1,2,3), _ + 1) shouldEqual List(2,3,4)
  }

  "monocle.scalazi._ brings all polymorphic Optic instances in scope for Scalaz classes" in {
    import monocle.function._
    import monocle.scalazi._

    // do not compile because Each instance for List is not in scope
    illTyped { """each[List[Int], Int].modify(List(1,2,3), _ + 1)""" }

    each[IList[Int], Int].modify(IList(1,2,3), _ + 1) shouldEqual IList(2,3,4)
  }

  "monocle.generic._ brings all polymorphic Optic instances in scope for Shapeless classes" in {
    import monocle.function._
    import monocle.generic._

    // do not compile because Each instance for List is not in scope
    illTyped { """each[List[Int], Int].modify(List(1,2,3), _ + 1)""" }

    head[Int :: HNil, Int].modify(1 :: HNil, _ + 1) shouldEqual (2 :: HNil)
  }

}


