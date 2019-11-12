package monocle

import monocle.function.Cons
import monocle.macros.{GenLens, GenPrism}
import monocle.syntax.all._
import monocle.macros.syntax._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class FieldSyntaxSpec extends AnyFunSuite with Matchers {

  case class Foo(i: Int, bar: Bar)
  case class Bar(b: Boolean, s: String)
  val foo = Foo(5, Bar(true, "Hello"))
  val fooBarLens = GenLens[Foo](_.bar)

  sealed trait ThisOrThat
  case class This(i: Int, bar: Bar) extends ThisOrThat
  case class That(d: Double) extends ThisOrThat

  val prism = GenPrism[ThisOrThat, This]
  val x = This(5, Bar(true, "Hello"))
  val thisOrThat: ThisOrThat = x

  implicit val cons: Cons[List[Bar]] = Cons.list[Bar]
  val optional = Optional.headOption[List[Bar], Bar]
  val bars = List(
    Bar(true, "a"),
    Bar(true, "b"),
    Bar(true, "c")
  )

  test("field syntax (Iso)") {
    Iso.id[Foo].field(_.i).get(foo) shouldEqual foo.i
    Iso.id[Foo].field(_.bar.b).get(foo) shouldEqual foo.bar.b
    Iso.id[Foo].field(_.bar).field(_.b).get(foo) shouldEqual foo.bar.b
  }

  test("field syntax (AppliedIso)") {
    foo.optic.field(_.i).get shouldEqual foo.i
    foo.optic.field(_.bar.b).get shouldEqual foo.bar.b
    foo.optic.field(_.bar).field(_.b).get shouldEqual foo.bar.b
  }

  test("field syntax (Lens)") {
    fooBarLens.field(_.b).get(foo) shouldEqual foo.bar.b
  }

  test("field syntax (AppliedLens)") {
    foo.optic(fooBarLens).field(_.b).get shouldEqual foo.bar.b
  }

  test("field syntax (Prism)") {
    prism.field(_.i).getOption(thisOrThat) shouldEqual Some(x.i)
    prism.field(_.bar.b).getOption(thisOrThat) shouldEqual Some(x.bar.b)
    prism.field(_.bar).field(_.b).getOption(thisOrThat) shouldEqual Some(x.bar.b)
  }

  test("field syntax (AppliedPrism)") {
    thisOrThat.optic(prism).field(_.i).getOption shouldEqual Some(x.i)
    thisOrThat.optic(prism).field(_.bar.b).getOption shouldEqual Some(x.bar.b)
    thisOrThat.optic(prism).field(_.bar).field(_.b).getOption shouldEqual Some(x.bar.b)
  }

  test("field syntax (Optional)") {
    optional.field(_.b).getOption(bars) shouldEqual Some(bars.head.b)
  }

  test("field syntax (AppliedOptional)") {
    bars.optic(optional).field(_.b).getOption shouldEqual Some(bars.head.b)
  }

}
