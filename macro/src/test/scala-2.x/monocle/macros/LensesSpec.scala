package monocle.macros

import cats.Eq
import munit.DisciplineSuite
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Arbitrary
import monocle.law.discipline._

@Lenses case class Example2(s: String, x: Int)
@Lenses case class Foo[A, B](q: Map[(A, B), Double], default: Double)

// a few more examples that should compile
@Lenses case class HasCompanion1[A](a: A)
object HasCompanion1

@Lenses case class HasCompanion2[A](a: A)
object HasCompanion2 { def foo = () }

trait Bar; trait Baz
@Lenses case class HasCompanion3[A](a: A)
object HasCompanion3 extends Bar with Baz

class annot extends annotation.StaticAnnotation
@Lenses case class HasCompanion4(l: Long, s: String)
@annot object HasCompanion4

class LensesSpec extends DisciplineSuite {
  implicit val exampleArb: Arbitrary[Example2] = Arbitrary(for {
    s <- arbitrary[String]
    x <- arbitrary[Int]
  } yield Example2(s, x))

  implicit val exampleEq: Eq[Example2] = Eq.fromUniversalEquals[Example2]

  checkAll("Lenses", LensTests(Example2.s))

}
