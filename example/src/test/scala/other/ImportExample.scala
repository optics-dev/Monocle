package other

import monocle.TestInstances
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.prop.Configuration
import org.typelevel.discipline.scalatest.FunSuiteDiscipline
import shapeless.test.illTyped
import shapeless.{::, HNil}
import org.scalatest.matchers.should.Matchers

case class Custom(value: Int)

object Custom {
  import monocle.Lens
  import monocle.function.Field1

  implicit val customHead = new Field1[Custom, Int] {
    def first = Lens((_: Custom).value)(v => c => c.copy(value = v))
  }
}

// Cannot use MonocleSuite as it brings all imports
class ImportExample extends AnyFunSuite with Configuration with FunSuiteDiscipline with Matchers with TestInstances {
  test("monocle.function.all._ imports all polymorphic optics in the scope") {
    import monocle.function.all._

    // do not compile because Each instance for List is not in the scope
    illTyped("""each[List[Int], Int].modify(List(1,2,3), _ + 1)""")

    each[List[Int], Int].modify(_ + 1)(List(1, 2, 3)) shouldEqual List(2, 3, 4)

    // also compile because Head instance for Custom is in the companion of Custom
    first[Custom, Int].modify(_ + 1)(Custom(1)) shouldEqual Custom(2)
  }

  test("monocle.syntax.all._ permits to use optics as operator which improves type inference") {
    import monocle.function.all._

    // do not compile because scala cannot infer which instance of Each is required
    illTyped("""each.modify(List(1,2,3), _ + 1)""")

    each[List[Int], Int].modify(_ + 1)(List(1, 2, 3)) shouldEqual List(2, 3, 4)
  }

  test("monocle.std.all._ brings all polymorphic Optic instances in scope for standard Scala classes") {
    import monocle.function.all._

    // do not compile because Head instance for HList is not in scope
    illTyped("""head[Int :: HNil, Int].modify(1 :: HNil, _ + 1) shouldEqual (2 :: HNil)""")

    each[List[Int], Int].modify(_ + 1)(List(1, 2, 3)) shouldEqual List(2, 3, 4)
  }

  test("monocle.generic.all._ brings all polymorphic Optic instances in scope for Shapeless classes") {
    import monocle.function.all._
    import monocle.generic.all._

    // do not compile because Each instance for List is not in scope
    illTyped("""each[List[Int], Int].modify(List(1,2,3), _ + 1)""")

    first[Int :: HNil, Int].modify(_ + 1)(1 :: HNil) shouldEqual (2 :: HNil)
  }

  test("monocle._, Monocle._ makes all Monocle core features available (no generic)") {
    import monocle._, Monocle._

    each[List[Int], Int].modify(_ + 1)(List(1, 2, 3)) shouldEqual List(2, 3, 4)
  }
}
