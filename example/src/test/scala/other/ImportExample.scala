package other

import monocle.{TestInstances, Traversal}
import shapeless.test.illTyped
import shapeless.{::, HNil}

case class Custom(value: Int)

object Custom {
  import monocle.Lens
  import monocle.function.Each

  implicit val customHead = new Each[Custom, Int] {
    def each: Traversal[Custom, Int] = Lens((_: Custom).value)(v => c => c.copy(value = v)).asTraversal
  }
}

// Cannot use MonocleSuite as it brings all imports
class ImportExample extends munit.FunSuite with TestInstances {
  test("monocle.function.all._ imports all polymorphic optics in the scope") {
    import monocle.function.all._

    // do not compile because Each instance for List is not in the scope
    illTyped("""each[List[Int], Int].modify(List(1,2,3), _ + 1)""")

    assertEquals(each[List[Int], Int].modify(_ + 1)(List(1, 2, 3)), List(2, 3, 4))

    // also compile because Each instance for Custom is in the companion of Custom
    assertEquals(each[Custom, Int].modify(_ + 1)(Custom(1)), Custom(2))
  }

  test("monocle.syntax.all._ permits to use optics as operator which improves type inference") {
    import monocle.function.all._

    // do not compile because scala cannot infer which instance of Each is required
    illTyped("""each.modify(List(1,2,3), _ + 1)""")

    assertEquals(each[List[Int], Int].modify(_ + 1)(List(1, 2, 3)), List(2, 3, 4))
  }

  test("monocle.std.all._ brings all polymorphic Optic instances in scope for standard Scala classes") {
    import monocle.function.all._

    // do not compile because Head instance for HList is not in scope
    illTyped("""assertEquals(head[Int :: HNil, Int].modify(1 :: HNil, _ + 1),  (2 :: HNil))""")

    assertEquals(each[List[Int], Int].modify(_ + 1)(List(1, 2, 3)), List(2, 3, 4))
  }

  test("monocle.generic.all._ brings all polymorphic Optic instances in scope for Shapeless classes") {
    import monocle.function.all._
    import monocle.generic.all._

    // do not compile because Each instance for List is not in scope
    illTyped("""each[List[Int], Int].modify(List(1,2,3), _ + 1)""")

    assertEquals(head[Int :: HNil, Int, HNil].modify(_ + 1)(1 :: HNil), (2 :: HNil))
  }

  test("monocle._, Monocle._ makes all Monocle core features available (no generic)") {
    import monocle._, Monocle._

    assertEquals(each[List[Int], Int].modify(_ + 1)(List(1, 2, 3)), List(2, 3, 4))
  }
}
