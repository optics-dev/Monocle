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
    def head = SimpleLens[Custom](_.value)((v, c) => c.copy(value = v))
  }
}

class ImportExample extends Spec {

  "monocle.function._ imports all polymorphic optics in the scope" in {
    import monocle.function._

    // do not compile because Each instance for List is not in the scope
    illTyped { """each[List[Int], Int].modify(List(1,2,3), _ + 1)""" }

    import monocle.std.list._
    each[List[Int], Int].modify(_ + 1)(List(1,2,3)) ==== List(2,3,4)

    // also compile because Head instance for Custom is in the companion of Custom
    head[Custom, Int].modify(_ + 1)(Custom(1)) ==== Custom(2)
  }

  "monocle.syntax._ permits to use optics as operator which improves type inference" in {
    import monocle.function._
    import monocle.std.list._
    import monocle.syntax._

    // do not compile because scala cannot infer which instance of Each is required
    illTyped { """each.modify(List(1,2,3), _ + 1)""" }

    each[List[Int], Int].modify(_ + 1)(List(1,2,3)) ==== List(2,3,4)
  }

  "monocle.std._ brings all polymorphic Optic instances in scope for standard Scala classes" in {
    import monocle.function._
    import monocle.std._

    // do not compile because Head instance for HList is not in scope
    illTyped { """head[Int :: HNil, Int].modify(1 :: HNil, _ + 1) ==== (2 :: HNil)""" }

    each[List[Int], Int].modify(_ + 1)(List(1,2,3))   ==== List(2,3,4)
    each[IList[Int], Int].modify(_ + 1)(IList(1,2,3)) ==== IList(2,3,4)
  }

  "monocle.generic._ brings all polymorphic Optic instances in scope for Shapeless classes" in {
    import monocle.function._
    import monocle.generic._

    // do not compile because Each instance for List is not in scope
    illTyped { """each[List[Int], Int].modify(List(1,2,3), _ + 1)""" }

    head[Int :: HNil, Int].modify(_ + 1)(1 :: HNil) ==== (2 :: HNil)
  }

  "monocle._, Monocle._ makes all Monocle core features available (no generic)" in {
    import monocle._, Monocle._

    each[List[Int], Int].modify(_ + 1)(List(1,2,3))   ==== List(2,3,4)
    each[IList[Int], Int].modify(_ + 1)(IList(1,2,3)) ==== IList(2,3,4)
  }

}


